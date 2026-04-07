package com.ui.lib;

import java.lang.Byte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import static java.util.Map.entry;
import java.util.Map;

public class PacketHeader {
    public final short SYNC = (short) 0xF35C; 
    public short PACKET_LENGTH;
    public byte PACKET_TYPE;
    public short SEQUENCE_NUMBER;

    public PacketHeader(byte PACKET_TYPE, short SEQUENCE_NUMBER, short PACKET_LENGTH) {
        this.PACKET_TYPE = PACKET_TYPE;
        this.SEQUENCE_NUMBER = SEQUENCE_NUMBER;
        this.PACKET_LENGTH = PACKET_LENGTH;
    }

    public PacketHeader(byte[] buffer) {
        ByteBuffer lengthBuff = ByteBuffer.allocate(2);
        lengthBuff.order(ByteOrder.LITTLE_ENDIAN);
        lengthBuff.put(buffer[2]);
        lengthBuff.put(buffer[3]);
        lengthBuff.flip();

        ByteBuffer sequenceBuff = ByteBuffer.allocate(2);
        sequenceBuff.order(ByteOrder.LITTLE_ENDIAN);
        sequenceBuff.put(buffer[5]);
        sequenceBuff.put(buffer[6]);
        sequenceBuff.flip();

        this.PACKET_TYPE = buffer[4];
        this.SEQUENCE_NUMBER = sequenceBuff.getShort();
        this.PACKET_LENGTH = lengthBuff.getShort();
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

    // The byte codes for packets that should be added to the map of crit packets
    // The byte codes for packets that will be received with unmatching sequence numbers
    //public static Set<Packet> DROP_CRITICAL_PACKETS = 
    //public static Set<Packet> OUT_OF_SEQ_PACKETS = 

    public byte[] toByteEncoding() {
        byte[] encoding = new byte[7];
        encoding[0] = (byte)(SYNC & 0xff);
        encoding[1] = (byte)((SYNC >> 8) & 0xff);
        encoding[2] = (byte)(PACKET_LENGTH & 0xff);
        encoding[3] = (byte)((PACKET_LENGTH >> 8) & 0xff);
        encoding[4] = PACKET_TYPE;
        encoding[5] = (byte)(SEQUENCE_NUMBER & 0xff);
        encoding[6] = (byte)((SEQUENCE_NUMBER >> 8) & 0xff);
        
        return encoding;
    }
}