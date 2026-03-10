package com.ui;

import static java.util.Map.entry;
import com.fazecast.jSerialComm.*;
import com.ui.NetworkService.Packet;
import com.ui.NetworkService.PacketHeader;

import java.lang.Byte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;   

public class NetworkService {
    private boolean portAvailable;
    private boolean connected;
    private final String TARGET_PORT_NAME = "UNKNOWN";
    private SerialPort port;

    private Packet[] pastPackets;
    public int sequence;

    public void ConnectOverPort() {
        Thread connectionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                while(!portAvailable) {
                    if(SerialPort.getCommPort(TARGET_PORT_NAME) != null) {
                        portAvailable = true;
                        port = SerialPort.getCommPort(TARGET_PORT_NAME);
                        port.openPort();
                    }
                }

                while(!connected) {
                    if(port.bytesAvailable() != 0) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        // TODO: Remove this section, move it over to the core logic and handle communication
                        // or connection detection in their with timeout logic, safe reading, etc.
                        try {
                            Packet packet = new Packet(readBuffer);
                            if(packet.HEADER.PACKET_TYPE == 0x07) {
                                connected = true;
                                startPortListen();
                                Thread.currentThread().interrupt();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Packet heartbeat = new Packet("HEARTBEAT", null);
                    byte[] heartbeatBuffer = heartbeat.toByteEncoding();
                    port.writeBytes(heartbeatBuffer, heartbeatBuffer.length);

                    try{ 
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });

        connectionThread.start();
    }

    private void startPortListen() {
        Thread portThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if(port.bytesAvailable() != 0) {

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
    
    public class PacketHeader {
        private final short SYNC = (short) 0xF35C; 
        private byte PACKET_TYPE;
        private short SEQUENCE_NUMBER;
        private short PACKET_LENGTH;

        public PacketHeader(byte PACKET_TYPE, short SEQUENCE_NUMBER, short PACKET_LENGTH) {
            this.PACKET_TYPE = PACKET_TYPE;
            this.SEQUENCE_NUMBER = SEQUENCE_NUMBER;
            this.PACKET_LENGTH = PACKET_LENGTH;
        }

        public PacketHeader(byte[] buffer) {
            ByteBuffer sequenceBuff = ByteBuffer.allocate(2);
            sequenceBuff.order(ByteOrder.LITTLE_ENDIAN);
            sequenceBuff.put(buffer[3]);
            sequenceBuff.put(buffer[4]);

            ByteBuffer lengthBuff = ByteBuffer.allocate(2);
            lengthBuff.order(ByteOrder.LITTLE_ENDIAN);
            lengthBuff.put(buffer[5]);
            lengthBuff.put(buffer[6]);

            this.PACKET_TYPE = buffer[2];
            this.SEQUENCE_NUMBER = sequenceBuff.getShort();
            this.PACKET_LENGTH = sequenceBuff.getShort();
        }

        public static Map<String, Byte> PACKET_TYPES = Map.ofEntries(
            entry("TELEMETRY_UPDATE", (byte) 0x00),
            entry("FLIGHTS_UPDATE", (byte) 0x01),
            entry("HEALTH_UPDATE", (byte) 0x02),
            entry("TESTS_UPDATE", (byte) 0x03),
            entry("COMMAND", (byte) 0x04),
            entry("CONTEXT_COMMAND", (byte) 0x05),
            entry("ACKNOWLEDGE", (byte) 0x06),
            entry("HEARTBEAT", (byte) 0x07),
            entry("PACKET_DROP_NOTICE", (byte) 0x08)
        );

        public byte[] toByteEncoding() {
            byte[] encoding = new byte[7];
            encoding[0] = (byte)(SYNC & 0xff);
            encoding[1] = (byte)((SYNC >> 8) & 0xff);
            encoding[2] = PACKET_TYPE;
            encoding[3] = (byte)(SEQUENCE_NUMBER & 0xff);
            encoding[4] = (byte)((SEQUENCE_NUMBER >> 8) & 0xff);
            encoding[5] = (byte)(PACKET_LENGTH & 0xff);
            encoding[6] = (byte)((PACKET_LENGTH >> 8) & 0xff);
            return encoding;
        }
    }

    public class Packet {
        private PacketHeader HEADER;
        private byte[] DATA;

        // Constructor options - Either with or without defined sequence num. On creation packet sequence is iterated up by one.
        // On send the global sequence is updated.
        public Packet(String packetType, byte[] DATA) {
            if(DATA != null) {
                HEADER = new PacketHeader(PacketHeader.PACKET_TYPES.get(packetType), (short) (sequence + 1), (short) DATA.length);
            } else {
                HEADER = new PacketHeader(PacketHeader.PACKET_TYPES.get(packetType), (short) (sequence + 1), (short) 0);
            }
        }

        public Packet(String packetType, int SEQUENCE_NUMBER, byte[] DATA) {
            HEADER = new PacketHeader(PacketHeader.PACKET_TYPES.get(packetType), (short) SEQUENCE_NUMBER, (short) DATA.length);
        }

        public Packet(byte[] buffer) {
            if(buffer.length < 7) {
                throw new IllegalArgumentException("Attempted packet creation with malformed header size");
            }
            
            HEADER = new PacketHeader(Arrays.copyOfRange(buffer, 0, 7));
            DATA = Arrays.copyOfRange(buffer, 0, buffer.length);
        }

        public byte[] toByteEncoding() {
            byte[] encoding = new byte[7 + DATA.length];
            byte[] headerEncoding = HEADER.toByteEncoding();

            for(int i = 0; i < 7; i++) {
                encoding[i] = headerEncoding[i];
            }

            for(int i = 7; i < encoding.length; i++) {
                encoding[i] = DATA[i];
            }

            return encoding;
        }
    }

    // --------------
    // PACKET HANDLING FUNCTIONS
    // --------------

    private void handleTelemetryPacket(byte[] telemetryBuffer) {
        // TODO: Add protobuf and extend to rest of packets.
    }


}

