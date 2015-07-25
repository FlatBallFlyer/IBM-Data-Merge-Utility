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
