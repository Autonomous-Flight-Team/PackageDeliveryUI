package com.ui;

import static java.util.Map.entry;
import com.fazecast.jSerialComm.*;
import com.ui.lib.*;

import javafx.concurrent.Task;

import java.lang.Byte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;   

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService {
    private boolean connected;
    private final String TARGET_PORT_NAME = "UNKNOWN";
    private SerialPort port;

    private Packet[] pastPackets;
    public int sequence;
    private boolean writingData;

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    // Wait for an available port to be present, checking every 1 second.
    public void ConnectOverPort() {
        Thread connectionThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                boolean portAvailable = false;

                while(!portAvailable) {
                    if(SerialPort.getCommPort(TARGET_PORT_NAME) != null) {
                        portAvailable = true;
                        port = SerialPort.getCommPort(TARGET_PORT_NAME);
                        port.openPort();
                    }

                    try{ 
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                ListenOnPort();
            }
        });

        connectionThread.start();
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
                    // CHECK FOR SYNC TODO: 
                    readPacketData();
                            
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

    // Sends a packet, avoiding sending while another packet is sending.
    public void SendPacket(Packet packet) {
        if(!port.isOpen()) {
            throw new IllegalStateException();
        }

        Task<Void> sendTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while(writingData) {
                    Thread.sleep(20);
                }

                writingData = true;
                byte[] packetBuffer = packet.toByteEncoding();
                port.writeBytes(packetBuffer, packetBuffer.length);

                // Insert this last written packet into the packet list
                pastPackets = Arrays.copyOfRange(pastPackets, 1, pastPackets.length);
                pastPackets[pastPackets.length - 1] = packet;

                writingData = false;
                return null;
            }
        };

        executor.submit(sendTask);
    }

    private void readPacketData() {
        if(!port.isOpen()) {
            throw new IllegalStateException();
        }
        
        byte[] lengthCheckBuffer = new byte[2];

        port.readBytes(lengthCheckBuffer, 2);

        ByteBuffer shortBuff = ByteBuffer.allocate(2);
        shortBuff.order(ByteOrder.LITTLE_ENDIAN);
        shortBuff.put(lengthCheckBuffer[0]);
        shortBuff.put(lengthCheckBuffer[1]);

        short length = shortBuff.getShort();

        byte[] packetBuffer = new byte[length + 7];
        packetBuffer[0] = (byte)(0xF35 & 0xff);
        packetBuffer[1] = (byte)((0xF35 >> 8) & 0xff);
        packetBuffer[2] = (byte)(length & 0xff);
        packetBuffer[3] = (byte)((length >> 8) & 0xff);

        port.readBytes(packetBuffer, length + 3, 4);
        Packet decodedPacket = new Packet(packetBuffer);

        handleReadPacket(decodedPacket);
    }

    //TODO: Implement specific packet handling functions.
    private void handleReadPacket(Packet packet) {
        switch(packet.HEADER.PACKET_TYPE) {
            case(0x00):
                break;
            case(0x01):
                break;
            case(0x02):
                break;
            case(0x03):
                break;
            case(0x04):
                break;
            case(0x05):
                break;
            case(0x06):
                break;
        }
    } 
    
    // --------------
    // PACKET HANDLING FUNCTIONS
    // --------------

    private void handleTelemetryPacket(byte[] telemetryBuffer) {
        // TODO: Add protobuf and extend to rest of packets.
    }
}