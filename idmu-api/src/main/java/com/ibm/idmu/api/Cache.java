package com.ibm.idmu.api;

import java.util.Map;

/**
 *
 */
public interface Cache<CacheKeyType, CachedItemType> {
    boolean isCached(CacheKeyType key);
    void evict(CacheKeyType key);
    void cache(CacheKeyType key, CachedItemType val);
    CachedItemType get(CacheKeyType key);
    void clear();
    int size();
    Map<CacheKeyType, CachedItemType> asMap();
}
