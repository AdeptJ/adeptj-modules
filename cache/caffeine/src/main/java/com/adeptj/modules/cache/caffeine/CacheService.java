package com.adeptj.modules.cache.caffeine;

public interface CacheService {

    <K, V> Cache<K, V> getCache(String name);

}
