package com.rws.lt.lc.blueprint.security.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU cache with max capacity. Needs to be synchronized externally.
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {

    static int DEFAULT_INITIAL_CAPACITY = 16;
    static float DEFAULT_LOAD_FACTOR = 0.75f;

    private final int maxEntries;

    public LRUMap(int maxEntries) {
        super(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, true);
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }
}