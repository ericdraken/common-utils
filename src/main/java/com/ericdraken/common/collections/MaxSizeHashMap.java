/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.collections;

import java.util.LinkedHashMap;
import java.util.Map;

// REF: https://stackoverflow.com/a/5601377/1938889
public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public MaxSizeHashMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}