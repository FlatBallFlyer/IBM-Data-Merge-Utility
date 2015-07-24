package com.ibm.util.merge.cache;

import com.ibm.idmu.api.MemoryCache;
import com.ibm.util.merge.template.CollectionName;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.TemplateName;

import java.util.*;

/**
 *
 */
public class TemplateCache extends MemoryCache<String, Template> {
    /**
     * @return list of Collection names found in cache
     */
    public ArrayList<CollectionName> getCollectionsFromCache() {
        HashMap<String,CollectionName> theCollections = new HashMap<String,CollectionName>();
        // Iterate the Hash
        for (Template template : asMap().values()) {
            if (!theCollections.containsKey(template.getCollection())) {
                theCollections.put(template.getCollection(), new CollectionName(template.getCollection()));
            }
        }
        // Return the Set
        ArrayList<CollectionName> theNames = new ArrayList<CollectionName>();
        theNames.addAll(theCollections.values());
        return theNames;
    }

    /**
     * @param collection - Template collection to get
     * @return list of Template Names in the collection
     */
    public ArrayList<TemplateName> getTemplateFullnamesFromCache(String collection) {
        ArrayList<TemplateName> theTemplates = new ArrayList<TemplateName>();
        // Iterate the Hash
        for (Template template : asMap().values()) {
            if (template.getCollection().equals(collection)) {
                theTemplates.add(new TemplateName(template));
            }
        }
        // Return the List
        return theTemplates;
    }

    /**
     * @param collection - Templates Collection to get
     * @param name - Template name to get
     * @return list of Template Names from the colleciton, matching name
     */
    public ArrayList<TemplateName> getTemplateFullnamesFromCache(String collection, String name) {
    	ArrayList<TemplateName> theTemplates = new ArrayList<TemplateName>();
        // Iterate the Hash
        for (Template template : asMap().values()) {
            if (template.getCollection().equals(collection) && template.getName().equals(name)) {
                theTemplates.add(new TemplateName(template));
            }
        }
        // Return the Set
        return theTemplates;
    }
    
    /**
     * @param collections - List of collection names
     * @return list of Templates from the colleciton, matching name
     */
    public ArrayList<Template> getTemplates(List<String> collections) {
    	ArrayList<Template> theTemplates = new ArrayList<Template>();
        // Iterate the Hash
        for (Template template : asMap().values()) {
            if (collections.contains(template.getCollection())) {
            	theTemplates.add(template.getMergable(new HashMap<String,String>()));
            }
        }
        // Return the Set
    	return theTemplates;
    }
}
