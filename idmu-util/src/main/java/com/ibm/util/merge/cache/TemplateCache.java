package com.ibm.util.merge.cache;

import com.ibm.idmu.api.MemoryCache;
import com.ibm.util.merge.template.Template;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class TemplateCache extends MemoryCache<String, Template> {
    /**
     * @return
     */
    public Set<String> getCollectionsFromCache() {
        Set<String> theCollections = new HashSet<>();
        // Iterate the Hash
        for (Template template : asMap().values()) {
            if (!theCollections.contains(template.getCollection())) {
                theCollections.add(template.getCollection());
            }
        }
        // Return the JSON String
        return theCollections;
    }

    /**
     * @param collection
     * @return
     */
    public Set<String> getTemplateFullnamesFromCache(String collection) {
        Set<String> theTemplates = new HashSet<>();
        // Iterate the Hash
        for (Template template : asMap().values()) {
            if (template.getCollection().equals(collection)) {
                theTemplates.add(template.getFullName());
            }
        }
        // Return the JSON String
        return theTemplates;
    }
}
