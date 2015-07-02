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
package com.ibm.util.merge;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements a Caching Template Factory as well as all aspects of Template Persistence
 *
 * @author Mike Storey
 */
final public class TemplateFactory {
    // Factory Constants
    private static final Logger log = Logger.getLogger(TemplateFactory.class.getName());
    public final String KEY_CACHE_RESET = Template.wrap("DragonFlyCacheReset");
    public final String KEY_CACHE_LOAD = Template.wrap("DragonFlyCacheLoad");
    public final String KEY_FULLNAME = Template.wrap("DragonFlyFullName");
    private final String DEFAULT_FULLNAME = "root.default.";
    private final FilesystemPersistence fs;
    private HibernatePersistence hp;
    private final ConcurrentHashMap<String, Template> templateCache = new ConcurrentHashMap<String, Template>();
    

    public TemplateFactory(FilesystemPersistence fs) {
        this.fs = fs;
    }

    /**********************************************************************************
     * <p>Template from Servlet request Constructor. Initiates a template based on http Servlet Request
     * parameters. Servlet request parameters are used to initilize the Replace hash. The
     * template to be used is specified on the KEY_FULLNAME parameter.
     * If no template is provided the DEFAULT_FULLNAME value is used.
     * The KEY_CACHE_RESET parameter will reset the Template Cache.
     * The KEY_CACHE_LOAD parameter will load all templates</p>
     *
     * @param request HttpServletRequest
     * @return Template The new Template object
     * @throws MergeException
     * @see Template
     */
    public Template getTemplate(Map<String, String[]> request) {
        HashMap<String, String> replace = new HashMap<String, String>();
        // Open the "Default" template if if not specified.
        replace.put(KEY_FULLNAME, DEFAULT_FULLNAME);
        // Iterate parameters, setting replace values
        for (String key : request.keySet()) {
            String value = request.get(key)[0];
            replace.put(Template.wrap(key), value);
        }
        // Handle cache reset request
        if (replace.containsKey(KEY_CACHE_RESET)) {
            reset();
        }
        // Handle cache load request
        if (replace.containsKey(KEY_CACHE_LOAD)) {
            loadTemplatesFromFilesystem();
        }
        // Get the template, add the http parameter replace values to it's hash
        String fullName = replace.get(KEY_FULLNAME);
        Template rootTempalte = getTemplate(fullName, "", replace);
        return rootTempalte;
    }

    public void loadTemplatesFromFilesystem() {
        List<Template> templates = fs.loadAll();
        for (Template t : templates) {
            cache(t);
        }
    }

    /**********************************************************************************
     * Get a copy of a cached Template based on a Template Fullname, which is comprised of
     * collection, name and columnValue (a unique key to the TEMPLATE table) The
     * provided Replace hash is copied to the new Template before it is returned
     *
     * @param fullName    - Template full name
     * @param shortName   - "Default" template name
     * @param seedReplace Initial replace hash
     * @return Template The new Template object
     * @throws MergeException Invalid Directive Type from Constructor
     */
    public Template getTemplate(String fullName, String shortName, HashMap<String, String> seedReplace) {
        Template newTemplate = null;
        // Cache hit -- Return a clone of the Template in Cache
        if (templateCache.containsKey(fullName)) {
            return templateCache.get(fullName).clone(seedReplace);
        }
        // See if FullName template is in the database
        if (this.hp != null) {
            newTemplate = hp.getTemplateFullname(fullName);
            templateCache.putIfAbsent(fullName, newTemplate);
            log.info("Constructed Template: " + fullName);
            return templateCache.get(fullName).clone(seedReplace);
//            }
        }
        // Check for shortName in the cache, since fullName doesn't exist
        if (templateCache.containsKey(shortName)) {
            templateCache.putIfAbsent(fullName, templateCache.get(shortName));
            log.info("Linked Template: " + shortName + " to " + fullName);
            return templateCache.get(fullName).clone(seedReplace);
        }
        // See if the shortName (Default Template) is in the database
        if (this.hp != null) {
            newTemplate = hp.getTemplateDefault(fullName);
            templateCache.putIfAbsent(fullName, newTemplate);
            log.info("Linked Template: " + shortName + " to " + fullName);
            return templateCache.get(fullName).clone(seedReplace);
        }
        // Template Not Found Exception
        throw new TemplateNotFoundException(fullName);
    }

    /**********************************************************************************
     * <p>Get a JSON List of Collections.</p>
     *
     * @param request HttpServletRequest
     * @return JSON List of Collection Names
     * @throws MergeException
     * @see Template
     */
    public String getCollections() {
        ArrayList<String> theList = new ArrayList<String>();
        if (this.hp != null) {
            theList = getCollectionsFromDb();
        } else {
            theList = getCollectionsFromCache();
        }
        Gson gson = new Gson();
        return gson.toJson(theList);
    }

    /**
     * @return
     */
    private ArrayList<String> getCollectionsFromCache() {
        ArrayList<String> theCollections = new ArrayList<String>();
        // Iterate the Hash
        for (Template template : templateCache.values()) {
            if (!theCollections.contains(template.getCollection())) {
                theCollections.add(template.getCollection());
            }
        }
        // Return the JSON String
        return theCollections;
    }

    /**
     * @return
     */
    private ArrayList<String> getCollectionsFromDb() {
        //String query = "DISTINCT COLLECTION";
        ArrayList<String> theTemplates = new ArrayList<String>();
        // Execute Query and populate returnTemplates list
        // Return the JSON String
        return theTemplates;
    }

    /**********************************************************************************
     * <p>Get a JSON List of Templates in a collection.</p>
     *
     * @param request HttpServletRequest
     * @return JSON List of Collection Names
     * @throws MergeException
     * @see Template
     */
    public String getTemplates(String collection) {
        ArrayList<String> theList = new ArrayList<String>();
        if (this.hp != null) {
            theList = getTemplatesFromDb(collection);
        } else {
            theList = getTemplatesFromCache(collection);
        }
        Gson gson = new Gson();
        return gson.toJson(theList);
    }

    /**
     * @param collection
     * @return
     */
    public ArrayList<String> getTemplatesFromCache(String collection) {
        ArrayList<String> theTemplates = new ArrayList<String>();
        // Iterate the Hash
        for (Template template : templateCache.values()) {
            if (template.getCollection().equals(collection)) {
                theTemplates.add(template.getFullName());
            }
        }
        // Return the JSON String
        return theTemplates;
    }

    /**
     * @param collection
     * @return
     */
    public ArrayList<String> getTemplatesFromDb(String collection) {
        //String query = "WHERE COLLECTION = :collection";
        ArrayList<String> theTemplates = new ArrayList<String>();
        // Execute Query and populate returnTemplates list
        // Return the JSON String
        return theTemplates;
    }

    /**********************************************************************************
     * Get a template as jSon, from cache or persistance (if enabled)
     *
     * @param String fullName - the Template Full Name
     */
    public String getTemplateAsJson(String fullName) {
        Template template = getTemplate(fullName, "", new HashMap<String, String>());
        return template.asJson(true);
    }

    /**********************************************************************************
     * Persist a template as jSon, to Disk system if persistance is not enabled.
     *
     * @param String json the template json
     */
    public String saveTemplateFromJson(String json) {
        Template template = cacheFromJson(json);
        if (this.hp != null) {
            hp.saveTemplateToDatabase(template);
        } else {
            fs.saveTemplateToJsonFolder(template);
        }
        return template.asJson(true);
    }

    /**********************************************************************************
     * Construct a template from a json formated string ad add it to the Cache
     *
     * @param request
     * @return the Template created
     * @throws MergeException - clone errors
     */
    public Template cacheFromJson(String jsonString) {
        Template template = Template.fromJSON(jsonString);
        return cache(template);
    }

    public Template cache(Template template) {
        templateCache.remove(template.getFullName());
        templateCache.putIfAbsent(template.getFullName(), template);
        log.info(template.getFullName() + " has been cached");
        return templateCache.get(template.getFullName()).clone(new HashMap<String, String>());
    }

    /**********************************************************************************
     * Reset the cache and Hibernate Connection
     *
     * @throws MergeException
     */
    public void reset() {
        log.warn("Template Cache / Hibernate Reset");
        templateCache.clear();
        if(this.hp != null){
            hp.initilizeHibernate();
        }

        log.info("Reset Complete");
    }

    /**********************************************************************************
     * Get the size of the template cache collection
     */
    public int size() {
        return templateCache.size();
    }



    public void setHp(HibernatePersistence hp) {
        this.hp = hp;
    }

    public static class TemplateNotFoundException extends RuntimeException {
        private String templateName;

        public TemplateNotFoundException(String templateName) {
            super("Could not find template : " + templateName);
            this.templateName = templateName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }
}