package com.ibm.util.merge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class MemoryCache<CacheKeyType, CachedItemType> {
    private final Map<CacheKeyType, CachedItemType> cache;

    public MemoryCache() {
        cache = new ConcurrentHashMap<>();
    }

    public MemoryCache(Map<CacheKeyType, CachedItemType> cache) {
        this.cache = new ConcurrentHashMap<>(cache);
    }

    public boolean isCached(CacheKeyType key) {
        return cache.containsKey(key);
    }

    public void evict(CacheKeyType key) {
        cache.remove(key);
    }

    public void cache(CacheKeyType key, CachedItemType val) {
        cache.put(key, val);
    }

    public CachedItemType get(CacheKeyType key) {
        return cache.get(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    /**
     * @return a copy of the cache contents as a java.util.Map
     */
    public Map<CacheKeyType, CachedItemType> asMap() {
        return new HashMap<>(cache);
    }
}
