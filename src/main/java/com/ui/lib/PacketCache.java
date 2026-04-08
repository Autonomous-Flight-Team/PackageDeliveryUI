package com.ui.lib;


import java.util.LinkedHashMap;
import java.util.Map;

public class PacketCache {
    private final static int CAPACITY = 256;
    private Map<Short, Packet> cache;

    public PacketCache() {
        cache = new LinkedHashMap<>(CAPACITY, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Short, Packet> eldest) {
                return size() > CAPACITY;
            }
        };
    }

    

    public void put(Packet p) {
        cache.put(p.HEADER.SEQUENCE_NUMBER, p); // getId() returns the short
    }

    public Packet get(short id) {
        return cache.get(id);
    }

    public boolean contains(short id) {
        return cache.containsKey(id);
    }
}
