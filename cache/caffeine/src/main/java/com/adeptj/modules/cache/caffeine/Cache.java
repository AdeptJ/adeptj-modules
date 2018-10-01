package com.adeptj.modules.cache.caffeine;

import java.util.Collection;
import java.util.Set;

public interface Cache<K,V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);

    void clear();

    long size();

    Set<K> keys();

    Collection<V> values();
}
