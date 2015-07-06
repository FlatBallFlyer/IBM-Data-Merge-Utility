package com.ibm.util.merge.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class MemoryCache<CacheKeyType, CachedItemType> implements Cache<CacheKeyType,CachedItemType> {
    private final Map<CacheKeyType, CachedItemType> cache;

    public MemoryCache() {
        cache = new ConcurrentHashMap<>();
    }

    public MemoryCache(Map<CacheKeyType, CachedItemType> cache) {
        this.cache = new ConcurrentHashMap<>(cache);
    }

    public MemoryCache(Cache<CacheKeyType, CachedItemType> cache) {
        this.cache = new ConcurrentHashMap<>(cache.asMap());
    }

    @Override
    public boolean isCached(CacheKeyType key) {
        return cache.containsKey(key);
    }

    @Override
    public void evict(CacheKeyType key) {
        cache.remove(key);
    }

    @Override
    public void cache(CacheKeyType key, CachedItemType val) {
        cache.put(key, val);
    }

    @Override
    public CachedItemType get(CacheKeyType key) {
        return cache.get(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }

    /**
     * @return a copy of the cache contents as a java.util.Map
     */
    @Override
    public Map<CacheKeyType, CachedItemType> asMap() {
        return new HashMap<>(cache);
    }
}
