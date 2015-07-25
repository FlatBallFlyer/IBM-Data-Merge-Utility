/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.idmu.api;

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
