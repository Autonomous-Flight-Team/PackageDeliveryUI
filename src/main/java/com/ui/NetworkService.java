
package com.ui;

import com.fazecast.jSerialComm.*;
import com.ui.lib.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;

// IMPLEMENTATION
// Network service - handles functions for doing network stuff, plus runs services
// Components
// - SendCommands (Using threadpool)
// - Heartbeat Sends commands on time
// - Packet receiving - handles recieving packets and managing logic
// - Listen and Connect - runs on the connection thread, handles setting up recieving packets
public class NetworkService {
    private boolean connected;
    private final String TARGET_PORT_NAME = "/dev/ttys001";
    private SerialPort port;

    private Packet[] pastPackets = new Packet[10];
    private Map<Integer, Packet> pastDropCriticalPackets;
    private LinkedBlockingQueue<Packet> packetQueue;

    private int txSequence;
    private int rxSequence;

    public NetworkService() {
        packetQueue = new LinkedBlockingQueue<>();
        ConnectOverPort();
    }

    // Wait for an available port to be present, checking every 1 second.
    public void ConnectOverPort() {
        Thread connectionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                boolean connected = false;
                
                while(!connected) {
                    port = SerialPort.getCommPort(TARGET_PORT_NAME);
                    connected = port.openPort();

                    if(!connected) {
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

                ListenOnPort();
                SendOnPort();
                StartHeartbeat();
                Thread.currentThread().interrupt();
            }
        });

        connectionThread.start();
    }

    public void StartHeartbeat() {
        Thread heartbeatThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(port.isOpen()) {
                    Packet heartbeatPacket = new Packet("HEARTBEAT", 0, null);
                    packetQueue.add(heartbeatPacket);
                    
                    System.out.println("Sending packet");
                } else {
                    System.out.println("Port closed");
                }         
            }
        });

        heartbeatThread.start();
    }

    // Sit on the open port - wait for 2 available bytes, check if it is the sync code, and if so
    // read into a packet.
    private void ListenOnPort() {
        if(!port.isOpen()) {
            throw new IllegalStateException();
        }

        Thread portThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
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
        });

        portThread.start();
    }

    // Start sending packets in packet queue
    public void SendOnPort() {
        if(!port.isOpen()) {
            throw new IllegalStateException();
        }

        Thread sendThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if(!packetQueue.isEmpty()) {
                    try {
                        Packet packet = packetQueue.take();
                        packet.HEADER.SEQUENCE_NUMBER = (short) txSequence;
                        byte[] packetBuffer = packet.toByteEncoding();
                        port.writeBytes(packetBuffer, packetBuffer.length);

                        // Insert this last written packet into the packet list
                        updatePastPackets(packet);
                        txSequence++;
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        sendThread.start();
    }

    // Inserts the passed packet into the end of the list of past packets, shifting all past packets
    // down and removing the oldest.
    private void updatePastPackets(Packet packet) {
        for(int i = 0; i < pastPackets.length - 1; i++) {
            pastPackets[i] = pastPackets[i + 1];
        }

        pastPackets[pastPackets.length - 1] = packet;
    }

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

        // Check for out of sequence packets
        // The packet type check is to ignore packets that have intentionally out of sequence numbers.
        // For example, the packet drop notice (0x08) will be out of order as it points to a past rxSequence, not txSequence
        if(decodedPacket.HEADER.SEQUENCE_NUMBER != (rxSequence + 1)) {
            if(decodedPacket.HEADER.SEQUENCE_NUMBER > rxSequence + 1 && 
                    decodedPacket.HEADER.PACKET_TYPE == 0x08 || decodedPacket.HEADER.PACKET_TYPE == 0x06) {
                for(int i = rxSequence + 1; i < decodedPacket.HEADER.SEQUENCE_NUMBER; i++) {
                    Packet dropNotice = new Packet("PACKET_DROP_NOTICE", i, null);
                    
                    packetQueue.add(dropNotice);
                }
            }
        }

        handleReadPacket(decodedPacket);
    }

    private void handleReadPacket(Packet packet) {
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
                handleHeartbeatPacket(packet);
                break;
            case(0x08):
                handlePacketDropPacket(packet);
                break;
        }
    } 
    
    // --------------
    // PACKET HANDLING FUNCTIONS
    // --------------

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
        // TODO: Add protobuf and extend to rest of packets.
    }

    private void handleCommandPacket(Packet packet) {
        // TODO: Add protobuf and extend to rest of packets.
    }

    private void handleContextCommandPacket(Packet packet) {
        // TODO: Add protobuf and extend to rest of packets.
    }

    private void handleCommandAckPacket(Packet packet) {
        // TODO: Add protobuf and extend to rest of packets.
    }

    private void handleHeartbeatPacket(Packet packet) {
        // Should update the counter of last heartbeat timestamp.
        // If this goes over 5s, an error will be thrown. Write the system that handles this.
    }

    private void handlePacketDropPacket(Packet packet) {
        // TODO: Add protobuf and extend to rest of packets.
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
