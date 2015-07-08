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

import com.ibm.util.merge.cache.TemplateCache;
import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;
import com.ibm.util.merge.persistence.HibernatePersistence;
import com.ibm.util.merge.template.Template;
import org.apache.log4j.Logger;

import java.util.*;

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
    private final TemplateCache templateCache;
    private final JsonProxy jsonProxy;

    public TemplateFactory(FilesystemPersistence fs) {
        this.fs = fs;
        templateCache = new TemplateCache();
        jsonProxy = new PrettyJsonProxy();
    }

    /**********************************************************************************
     * <p>Template from Servlet request Constructor. Initiates a template based on http Servlet Request
     * parameters. Servlet request parameters are used to initialize the Replace hash. The
     * template to be used is specified on the KEY_FULLNAME parameter.
     * If no template is provided the DEFAULT_FULLNAME value is used.
     * The KEY_CACHE_RESET parameter will reset the Template Cache.
     * The KEY_CACHE_LOAD parameter will load all templates</p>
     *
     * @param request HttpServletRequest
     * @return Template The new Template object
     * @see Template
     */
    public Template getTemplate(Map<String, String[]> request) {
        HashMap<String, String> replace = new HashMap<>();
        // Open the "Default" template if if not specified.
        replace.put(KEY_FULLNAME, DEFAULT_FULLNAME);
        // Iterate parameters, setting replace values
        for (String key : request.keySet()) {
            String value = request.get(key)[0];
            replace.put(Template.wrap(key), value);
        }
        // Handle cache reset request
        if (replace.containsKey(KEY_CACHE_RESET)) {
            log.info("requested RESET");
            reset();
        }
        // Handle cache load request
        if (replace.containsKey(KEY_CACHE_LOAD)) {
            log.info("requested LOAD TEMPLATES");
            loadTemplatesFromFilesystem();
        }
        // Get the template, add the http parameter replace values to it's hash
        String fullName = replace.get(KEY_FULLNAME);
        log.info("GET TEMPLATE = " + fullName);
        Template rootTemplate = getTemplate(fullName, "", replace);
        if (rootTemplate == null) {
            throw new IllegalArgumentException("Could not find template for request " + new HashMap<>(request).toString());
        }
        return rootTemplate;
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
     */
    public Template getTemplate(String fullName, String shortName, Map<String, String> seedReplace) {
        Template newTemplate = null;
        // Cache hit -- Return a clone of the Template in Cache
        if (templateCache.isCached(fullName)) {
            newTemplate = templateCache.get(fullName).clone(seedReplace);
        }
        // See if FullName template is in the database
        if (newTemplate == null && hp != null) {
            newTemplate = hp.getTemplateFullname(fullName);
            templateCache.cache(fullName, newTemplate);
            log.info("Constructed Template: " + fullName);
            newTemplate = templateCache.get(fullName).clone(seedReplace);
//            }
        }
        // Check for shortName in the cache, since fullName doesn't exist
        if (newTemplate == null && templateCache.isCached(shortName)) {
            templateCache.cache(fullName, templateCache.get(shortName));
            log.info("Linked Template: " + shortName + " to " + fullName);
            newTemplate = templateCache.get(fullName).clone(seedReplace);
        }
        // See if the shortName (Default Template) is in the database
        if (newTemplate == null && hp != null) {
            newTemplate = hp.getTemplateDefault(fullName);
            templateCache.cache(fullName, newTemplate);
            log.info("Linked Template: " + shortName + " to " + fullName);
            newTemplate = templateCache.get(fullName).clone(seedReplace);
        }
        if (newTemplate == null) {
            throw new TemplateNotFoundException(fullName);
        }
        return newTemplate;
    }

    /**********************************************************************************
     * <p>Get a JSON List of Collections.</p>
     *
     * @param request HttpServletRequest
     * @return JSON List of Collection Names
     * @throws MergeException
     * @see Template
     */
    public String getCollectionNamesJSON() {
        Set<String> theList = getCollectionNames();
        return jsonProxy.toJson(theList);
    }

    public Set<String> getCollectionNames() {
        Set<String> theList;// = new ArrayList<String>();
        if (hp != null) {
            theList = getCollectionsFromDb();
        } else {
            theList = templateCache.getCollectionsFromCache();
        }
        return theList;
    }

    /**
     * @return
     */
    private Set<String> getCollectionsFromDb() {
        //String query = "DISTINCT COLLECTION";
        Set<String> theTemplates = new HashSet<>();
        // TODO:Execute Query and populate returnTemplates list
        return theTemplates;
    }

    /**********************************************************************************
     * <p>Get a JSON List of Templates in a collection.</p>
     *
     * @param request HttpServletRequest
     * @return JSON List of Collection Names
     * @see Template
     */
    public String getTemplateNamesJSON(String collection) {
        Set<String> theList = getTemplateNames(collection);
        return jsonProxy.toJson(theList);
    }

    public Set<String> getTemplateNames(String collection) {
        Set<String> theList;// = new ArrayList<String>();
        if (hp != null) {
            theList = getTemplatesFromDb(collection);
        } else {
            theList = templateCache.getTemplateFullnamesFromCache(collection);
        }
        return theList;
    }

    /**
     * @param collection
     * @return
     */
    public Set<String> getTemplatesFromDb(String collection) {
        //String query = "WHERE COLLECTION = :collection";
        Set<String> theTemplates = new HashSet<>();
        // TODO: Execute Query and populate returnTemplates list
        return theTemplates;
    }

    /**********************************************************************************
     * Get a template as jSon, from cache or persistance (if enabled)
     *
     * @param String fullName - the Template Full Name
     */
    public String getTemplateAsJson(String fullName) {
        Template template = getTemplateByFullname(fullName);
        return jsonProxy.toJson(template);
    }

    private Template getTemplateByFullname(String fullName) {
        return getTemplate(fullName, "", new HashMap<>());
    }

    /**********************************************************************************
     * Persist a template as jSon, to Disk system if persistance is not enabled.
     *
     * @param String json the template json
     */
    public String saveTemplateFromJson(String json) {
        Template template1 = jsonProxy.fromJSON(json, Template.class);
        Template template = cache(template1);
        persistTemplate(template);
        return jsonProxy.toJson(template);
    }

    public void persistTemplate(Template template) {
        if (hp != null) {
            hp.saveTemplateToDatabase(template);
        } else {
            fs.saveTemplateToJsonFolder(template);
        }
    }

    public void deleteTemplate(Template template) {
        templateCache.evict(template.getFullName());
        fs.deleteTemplateOnFilesystem(template);
        if (hp != null) {
            hp.deleteTemplate(template);
        }
    }

    public Template cache(Template template) {
//        templateCache.remove(template.getFullName());
        templateCache.cache(template.getFullName(), template);
        log.info(template.getFullName() + " has been cached");
        Template clone = templateCache.get(template.getFullName()).clone(new HashMap<>());
        return clone;
    }

    /**********************************************************************************
     * Reset the cache and Hibernate Connection
     *
     * @throws MergeException
     */
    public void reset() {
        log.warn("Template Cache / Hibernate Reset");
        templateCache.clear();
        if (hp != null) {
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

    public Map<String, List<Template>> mapTemplatesPerCollection() {
        ArrayList<String> collectionNames = new ArrayList<>(getCollectionNames());
        Collections.sort(collectionNames);
        Map<String, List<Template>> collectionTemplatesMap = new TreeMap<>();
        for (String name : collectionNames) {
            Set<String> templateNames = new TreeSet<>(getTemplateCache().getTemplateFullnamesFromCache(name));
            List<Template> templates = new ArrayList<>();
            for (String templateName : templateNames) {
                Template template = getTemplateCache().get(templateName);
                templates.add(template);
            }
            collectionTemplatesMap.put(name, templates);
        }
        return collectionTemplatesMap;
    }

    public List<Template> listAllTemplates() {
        Map<String, List<Template>> collectionTemplatesMap = mapTemplatesPerCollection();
        List<Template> allTemplates = new ArrayList<>();
        for (Map.Entry<String, List<Template>> e : collectionTemplatesMap.entrySet()) {
//                String colname = e.getKey();
            List<Template> templates = e.getValue();
            allTemplates.addAll(templates);
        }
        return allTemplates;
    }

    public Template findTemplateForFullname(String fullName) {
        List<Template> collectionTemplates = listAllTemplates();
        Template found = null;
        for (Template template : collectionTemplates) {
            if (template.getFullName().equals(fullName)) {
                if (found != null) {
                    throw new IllegalStateException("Duplicate template found for fullName=" + fullName);
                }
                found = template;
            }
        }
        return found;
    }

    public boolean templateFullnameExists(String fullName) {
        return findTemplateForFullname(fullName) != null;
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

    public FilesystemPersistence getFs() {
        return fs;
    }

    public TemplateCache getTemplateCache() {
        return templateCache;
    }
}