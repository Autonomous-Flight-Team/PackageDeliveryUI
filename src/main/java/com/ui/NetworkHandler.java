package com.ui;

import com.fazecast.jSerialComm.*;
import com.ui.lib.*;

import javafx.application.Platform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

// IMPLEMENTATION
// State machine.
// Maintains four different states: Disconnected, Connecting, Connected, Connection Loss.
// Each state changes behaviour on how data is managed within network service.

/*
Functionality each state needs to handle -
Listening on the port - Maybe this could be done externally, and decoded packets sent to the network
service? Maybe this shouldn't be called network service - maybe NetworkHandler, with NetworkService
maintaining the connection to the port... Would need to be started and stopped when connecting is initiated.

Sending packets
Getting packets

both need to be managed..
*/

enum NetworkState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    CONNECTION_LOSS
}

enum NetworkEvent {
    CONNECTION_TIMEOUT,
    CONNECTION_SUCCESS,
    DISCONNECT_SUCCESS,
    TRY_CONNECT,
    TRY_DISCONNECT,
    CONNECTION_RESTABLISH
}

public class NetworkHandler { 
    // CONSTANTS MOVE TO SETTINGS TODO:
    private float communicationTimeout = 5.0f;
    private float connectionTimeout = 10.0f;
    private float timeSinceLastPacket = 0.0f;

    private NetworkState networkState;
    private NetworkService networkService;
    private Logging logging;

    private int rxSequence;
    private int txSequence;

    private final String TARGET_PORT_NAME = "/dev/ttys001";

    private static Set<Byte> DROP_CRITICAL_PACKETS = new HashSet<Byte>(Arrays.asList((byte) 0x04, (byte) 0x05, (byte) 0x10));
    private static Set<Byte> OUT_OF_SEQ_PACKETS = new HashSet<Byte>(Arrays.asList((byte) 0x06, (byte) 0x07, (byte) 0x09));

    private PacketCache txPacketCache;

    public NetworkHandler(Logging logging) {
        this.logging = logging;
        networkState = NetworkState.DISCONNECTED;
        networkService = new NetworkService(TARGET_PORT_NAME);
        KickstartMyHeart();
        RXListener();
    }

    // --------------
    // THREADS
    // --------------

    private void KickstartMyHeart() {
        Thread HeartbeatThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if(networkService.port.isOpen()) {
                    Packet heartbeatPacket = new Packet("HEARTBEAT", 0, null);
                    HandleTXPacket(heartbeatPacket);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        HeartbeatThread.start();
    }

    private void RXListener() {
        Thread RXThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if(!networkService.packetRXQueue.isEmpty()) {
                    timeSinceLastPacket = 0;
                    while(!networkService.packetRXQueue.isEmpty()) {
                        try {
                            Packet p = networkService.packetRXQueue.take();
                            HandleRXPacket(p);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Thread.sleep(100);
                        timeSinceLastPacket += .1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(timeSinceLastPacket > getTimeout()) {
                        HandleEvent(NetworkEvent.CONNECTION_TIMEOUT);
                    }
                }
            }
        });

        RXThread.start();
    }

    // --------------
    // STATE DEPENDENT FUNCTIONS
    // --------------

    // When trying to send a packet - push it into handle tx packet. Based on state, decide whether
    // to add that to the send queue or not.
    public void HandleTXPacket(Packet p) {
        switch(networkState) {
            case DISCONNECTED:
                logging.logError("Attempting to send packet while disconnected");
                break;
            case CONNECTING:
                // Send the packet across if its a heartbeat
                if(p.HEADER.PACKET_TYPE == PacketHeader.PACKET_TYPES.get("Heartbeat")) {
                    handleSendPacket(p);
                } else {
                    logging.logError("Attempting to send non-heartbeat packet while connecting");
                }
                break;
            case CONNECTED:
                handleSendPacket(p);
                break;
            case CONNECTION_LOSS:
                handleSendPacket(p);
                break;
        }
    }

    // When the network service receives a new packet - pushes it into HandleRxPacket. Then, reads packets
    // off of the RXQueue
    public void HandleRXPacket(Packet p) {
        switch(networkState) {
            case DISCONNECTED:
                logging.logError("Receieved network command while disconnected");
                break;
            case CONNECTING:
                HandleEvent(NetworkEvent.CONNECTION_SUCCESS);
                rxSequence = (p.HEADER.SEQUENCE_NUMBER - 1) % 65536; // Update rxSequence to packet - 1, then wrap properly.
                handleReadPacket(p);
                break;
            case CONNECTED:
                handleReadPacket(p);
                break;
            case CONNECTION_LOSS:
                HandleEvent(NetworkEvent.CONNECTION_RESTABLISH);
                handleReadPacket(p);
                break;
        }
    }

    // Event Handling
    public void HandleEvent(NetworkEvent event) {
        switch(event) {
            case CONNECTION_TIMEOUT:
                handleConnectionTimeout();
                break;
            case CONNECTION_SUCCESS:
                handleConnectionSuccess();
                break;
            case DISCONNECT_SUCCESS:
                handleDisconnectSuccess();
                break;
            case TRY_CONNECT:
                handleTryConnect();
                break;
            case TRY_DISCONNECT:
                handleTryDisconnect();
                break;
            case CONNECTION_RESTABLISH:
                handleConnectionRestablished();
                break;
        }
    }

    private void handleConnectionTimeout() {
        if(networkState == NetworkState.CONNECTED) {
            networkState = NetworkState.CONNECTION_LOSS;
        } else if(networkState == NetworkState.CONNECTING) {
            networkState = NetworkState.DISCONNECTED;
        } else {
            logging.logError("Connection timeout out of state");
        }
    }

    private void handleConnectionSuccess() {
        if(networkState == NetworkState.CONNECTING) {
            networkState = NetworkState.CONNECTED;
        } else {
            logging.logError("Connection success out of state");
        }
    }

    private void handleDisconnectSuccess() {
        if(networkState != NetworkState.DISCONNECTED) {
            networkState = NetworkState.DISCONNECTED;
        } else {
            logging.logError("Disconnect success out of state");
        }
    }

    private void handleTryConnect() {
        if(networkState == NetworkState.DISCONNECTED) {
            networkState = NetworkState.CONNECTING;
        } else {
            logging.logError("Attempting to connect out of state");
        }  
    }

    private void handleTryDisconnect() {
        if(networkState != NetworkState.DISCONNECTED) {
            HandleTXPacket(new Packet("DISCONNECT_REQUEST", txSequence, null));
        } else {
            logging.logError("Disconnect success out of state");
        }
    }

    private void handleConnectionRestablished() {
        if(networkState == NetworkState.CONNECTION_LOSS) {
            networkState = NetworkState.CONNECTED;
        } else {
            logging.logError("Restablished connection out of loss state");
        }
    }

    // Time since last packet to invoke a connection timeout event
    // This only makes sense for connecting and connected states, as no timeout logic would be handled
    // for disconnected and connection loss states.
    private float getTimeout() {
        switch(networkState) {
            case CONNECTING:
                return connectionTimeout;
            case CONNECTED:
                return communicationTimeout;
            default:
                logging.logError("Sampling timeout out of state");
                return 100000000;
        }
    }

    // --------------
    // PACKET HANDLING FUNCTIONS
    // --------------

    private void handleSendPacket(Packet p) {
        if(!OUT_OF_SEQ_PACKETS.contains(p.HEADER.PACKET_TYPE)) {
            p.HEADER.SEQUENCE_NUMBER = (short) (txSequence + 1);
            txSequence++;
            txPacketCache.put(p);
        }
        
        networkService.packetTXQueue.add(p);
    }

    private void handleAheadSequence(int packetSequenceDelta) {
        for(int i = rxSequence; i < rxSequence + packetSequenceDelta; i++) {
            short seq = (short) (i % 65536);
            HandleTXPacket(new Packet("PACKET_DROP_NOTICE", seq, null));
        }

        rxSequence = (rxSequence + packetSequenceDelta) % 65536;
    }
    
    private void handleReadPacket(Packet packet) {
        int packetSequenceDelta = (packet.HEADER.SEQUENCE_NUMBER - rxSequence) & 0xFFFF;
        if(packetSequenceDelta > 32768) { packetSequenceDelta -= 65536; }
        
        if(packetSequenceDelta > 1) {
            handleAheadSequence(packetSequenceDelta);
        }

        if(packetSequenceDelta > 0) {
            rxSequence = packet.HEADER.SEQUENCE_NUMBER;
        }

        switch(packet.HEADER.PACKET_TYPE) {
            case(0x00):
                handleTelemetryPacket(packet);
                break;
            case(0x01):
                handleFlightsPacket(packet);
                break;
            case(0x02):
                handleHealthPacket(packet);
                break;
            case(0x03):
                handleTestsPacket(packet);
                break;
            case(0x04):
                handleCommandPacket(packet);
                break;
            case(0x05):
                handleContextCommandPacket(packet);
                break;
            case(0x06):
                handleCommandAckPacket(packet);
                break;
            case(0x07):
                handleCommandRejPacket(packet);
                break;
            case(0x08):
                handleHeartbeatPacket(packet);
                break;
            case(0x09):
                handlePacketDropPacket(packet);
                break;
            case(0x10):
                handleDisconnectRequestPacket(packet);
                break;
        }
    } 
    
    private void handleTelemetryPacket(Packet packet) {
        // 
    }

    private void handleTestsPacket(Packet packet) {
        // TODO: Add protobuf and extend to rest of packets.
    }

    private void handleHealthPacket(Packet packet) {
        // TODO: Add protobuf and extend to rest of packets.
    }

    private void handleFlightsPacket(Packet packet) {
        // TODO: Decode with protobuf, update data, and then make SURE TO SEND OUT FLIGHT UPDATE
        Platform.runLater(() -> {sendMessage("FLIGHT_UPDATE");});
    }

    private void handleCommandPacket(Packet packet) {
        logging.logError("Received a command packet as ground station");
    }

    private void handleContextCommandPacket(Packet packet) {
        logging.logError("Received a command packet as ground station");
    }

    private void handleCommandAckPacket(Packet packet) {
        // TODO: Add protobuf and extend to rest of packets.
    }

    private void handleCommandRejPacket(Packet packet) {
        // TODO: Add protobuf and extend to rest of packets.
    }

    private void handleHeartbeatPacket(Packet packet) {
        // Do nothing
    }

    private void handlePacketDropPacket(Packet packet) {
        short dropSequence = packet.HEADER.SEQUENCE_NUMBER;

        if(txPacketCache.contains(dropSequence)) {
            HandleTXPacket(txPacketCache.get(dropSequence));
            logging.logInfo("Dropped drop-critical packet, resending");
        }

        logging.logInfo("Dropped non-critical packet, ignoring");
    }

    private void handleDisconnectRequestPacket(Packet packet) {
        
    }



    private class NetworkService {
        public SerialPort port;

        private LinkedBlockingQueue<Packet> packetTXQueue; // Transmit queue. Read by network service.
        private LinkedBlockingQueue<Packet> packetRXQueue; // Received queue. Read by network service, written by handler.

        public NetworkService(String portAddress) {
            packetTXQueue = new LinkedBlockingQueue<>();
            packetRXQueue = new LinkedBlockingQueue<>();
            
            ConnectOverPort(portAddress);
            StartRX();
            StartTX();
        }

        // --------------
        // THREADS
        // --------------

        private void ConnectOverPort(String portAddress) {
            Thread connectionThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    boolean portAvailable = false;
                    while(!portAvailable) {
                        port = SerialPort.getCommPort(portAddress);
                        portAvailable = port.openPort();

                        if(!portAvailable) {
                            try{ 
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }

                    System.out.println("DEBUG - Connected to port");
                    System.out.println(port.getSystemPortName());
                    System.out.println(port.isOpen());

                    Thread.currentThread().interrupt();
                }
            });

        connectionThread.start();
    }
    
    private void StartRX() {
        Thread RXThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if(port.isOpen()) {
                    if(port.bytesAvailable() > 1) {
                        byte[] readBuffer = new byte[2];

                        port.readBytes(readBuffer, 2);

                        ByteBuffer shortBuff = ByteBuffer.allocate(2);
                        shortBuff.order(ByteOrder.LITTLE_ENDIAN);
                        shortBuff.put(readBuffer[0]);
                        shortBuff.put(readBuffer[1]);
                        shortBuff.flip();

                        if((shortBuff.getShort() & 0xFFFF) == 0xF35c) {
                            readPacketData();
                        }       
                    } else {
                        try{ 
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        });

        RXThread.start();
    }

    private void StartTX() {
        Thread TXThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if(port.isOpen() && !packetTXQueue.isEmpty()) {
                     try {
                        Packet packet = packetTXQueue.take();
                        byte[] packetBuffer = packet.toByteEncoding();
                        port.writeBytes(packetBuffer, packetBuffer.length);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        TXThread.start();
    }

    // --------------
    // PACKET FUNCTIONS
    // --------------

    private void readPacketData() {
        if(!port.isOpen()) {
            throw new IllegalStateException();
        }
        
        byte[] lengthCheckBuffer = new byte[2];

        port.readBytes(lengthCheckBuffer, 2);

        short length = byteArrayToShort(lengthCheckBuffer);

        byte[] packetBuffer = new byte[length + 7];
        packetBuffer[0] = (byte)(0xF35C & 0xff);
        packetBuffer[1] = (byte)((0xF35C >> 8) & 0xff);
        packetBuffer[2] = lengthCheckBuffer[0];
        packetBuffer[3] = lengthCheckBuffer[1];

        port.readBytes(packetBuffer, length + 3, 4);
        Packet decodedPacket = new Packet(packetBuffer);
        
        packetRXQueue.add(decodedPacket);
    }

    // --------------
    // HELPERS
    // --------------

    private short byteArrayToShort(byte[] byteArr) {
        if(byteArr.length != 2) {
            throw new IllegalArgumentException();
        }

        ByteBuffer shortBuff = ByteBuffer.allocate(2);
        shortBuff.order(ByteOrder.LITTLE_ENDIAN);
        shortBuff.put(byteArr[0]);
        shortBuff.put(byteArr[1]);
        shortBuff.flip();

        return shortBuff.getShort();
    }

    private byte[] shortToByteArray(short s) {
        byte[] byteArr = new byte[2];
        byteArr[0] = (byte)(s & 0xff);
        byteArr[1] = (byte)((s >> 8) & 0xff);
        return byteArr;
    }

    }
}