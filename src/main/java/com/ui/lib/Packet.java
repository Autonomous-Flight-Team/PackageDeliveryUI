package com.ui.lib;

import com.ui.lib.*;

import java.lang.Byte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Packet {
    public PacketHeader HEADER;
    public byte[] DATA;

    // Constructor options - Either with or without defined sequence num. On creation packet sequence is iterated up by one.
    // On send the global sequence is updated.
    public Packet(String packetType, int sequence, byte[] DATA) {
        if(DATA != null) {
            HEADER = new PacketHeader(PacketHeader.PACKET_TYPES.get(packetType), (short) (sequence), (short) DATA.length);
        } else {
            HEADER = new PacketHeader(PacketHeader.PACKET_TYPES.get(packetType), (short) (sequence + 1), (short) 0);
            this.DATA = new byte[0];
        }
    }

    public Packet(byte[] buffer) {
        if(buffer.length < 7) {
            throw new IllegalArgumentException("Attempted packet creation with malformed header size");
        }
        
        HEADER = new PacketHeader(Arrays.copyOfRange(buffer, 0, 7));
        DATA = Arrays.copyOfRange(buffer, 0, buffer.length);
    }

    public byte[] toByteEncoding() {
        try {
            byte[] encoding = new byte[7 + DATA.length];
            byte[] headerEncoding = HEADER.toByteEncoding();

            for(int i = 0; i < 7; i++) {
                encoding[i] = headerEncoding[i];
            }

            for(int i = 7; i < encoding.length; i++) {
                encoding[i] = DATA[i - 7];
            }

            return encoding;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}