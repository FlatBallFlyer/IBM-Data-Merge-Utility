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

import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.cache.TemplateCache;
import com.ibm.util.merge.directive.Directives;
import com.ibm.util.merge.directive.Directives.DirectiveName;
import com.ibm.util.merge.persistence.AbstractPersistence;
import com.ibm.util.merge.template.CollectionName;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.TemplateName;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.jar.Attributes.Name;

/**
 * This class implements a Caching Template Factory as well as all aspects of Template Persistence
 *
 * @author Mike Storey
 */
final public class TemplateFactory {
    // Factory Constants
    private static final Logger log = Logger.getLogger(TemplateFactory.class.getName());
    public static final String KEY_CACHE_RESET = Template.wrap("DragonFlyCacheReset");
    public static final String KEY_FULLNAME = Template.wrap("DragonFlyFullName");
    public static final String DEFAULT_FULLNAME = "root.default.";
    private final AbstractPersistence persistence;
    private final File outputRoot;
	private final TemplateCache templateCache;
    private final JsonProxy jsonProxy;

    public TemplateFactory(AbstractPersistence persist, JsonProxy proxy, File outputRootDir) {
        this.templateCache = new TemplateCache();
        this.jsonProxy = proxy;
        this.persistence = persist;
        this.outputRoot = outputRootDir;
        reset();
    }

    /**********************************************************************************
     * <p>Template from Servlet request Constructor. Initiates a template based on http Servlet Request
     * parameters. Servlet request parameters are used to initialize the Replace hash. The
     * template to be used is specified on the KEY_FULLNAME parameter.
     * If no template is provided the DEFAULT_FULLNAME value is used.
     * The KEY_CACHE_RESET parameter will reset the Template Cache.
     * The KEY_CACHE_LOAD parameter will load all templates</p>
     *
     * @param requestParameters HttpServletRequest
     * @return Template The new Template object
     * @see Template
     */
    public String getMergeOutput(Map<String, String[]> requestParameters) {
        HashMap<String, String> replace = new HashMap<>();
        // Open the "Default" template if if not specified.
        replace.put(KEY_FULLNAME, DEFAULT_FULLNAME);
        // Iterate parameters, setting replace values
        for (String key : requestParameters.keySet()) {
            String value = requestParameters.get(key)[0];
            replace.put(Template.wrap(key), value);
        }
        // Handle cache reset request
        if (replace.containsKey(KEY_CACHE_RESET)) {
            log.info("requested RESET");
            reset();
        }

        // Setup and Merge the template
        MergeContext rtc = new MergeContext(this, replace);
        String returnValue;
        try {
            String fullName = replace.get(KEY_FULLNAME);
            log.info("GET TEMPLATE = " + fullName);
            Template rootTemplate = getTemplate(fullName, "", replace);
            returnValue = rootTemplate.getMergedOutput(rtc);
		} catch (MergeException e) {
			returnValue = rtc.getHtmlErrorMessage(e);
		} finally {
	        try {
				rtc.finalize();
			} catch (Exception e) {
				log.error("Context Finalize Error:" + e.getLocalizedMessage());
			}
		}
        return returnValue;
    }

    /**********************************************************************************
     * Get a copy of a cached Template based on a Template Fullname amd ShortName The
     * provided Replace hash is copied to the new Template before it is returned
     *
     * @param fullName    - Template full name
     * @param shortName   - "Default" template name
     * @param seedReplace Initial replace hash
     * @return Template The new Template object
     * @throws MergeException 
     */
    public Template getTemplate(String fullName, String shortName, Map<String, String> seedReplace) throws MergeException {
        Template newTemplate = null;
        // Cache hit -- Return a clone of the Template in Cache
        if (templateCache.isCached(fullName)) {
            newTemplate = templateCache.get(fullName).clone(seedReplace);
        }
        // Check for shortName in the cache, since fullName doesn't exist
        if (newTemplate == null && templateCache.isCached(shortName)) {
            templateCache.cache(fullName, templateCache.get(shortName));
            log.info("Linked Template: " + shortName + " to " + fullName);
            newTemplate = templateCache.get(fullName).clone(seedReplace);
        }
        if (newTemplate == null) {
            throw new MergeException("Template Not Found", fullName);
        }
        return newTemplate;
    }

    /**********************************************************************************
     * Get a template as jSon from cache
     *
     * @param String fullName - the Template Full Name
     */
    public String getTemplateAsJson(String fullName) {
        Template template;
		try {
			template = getTemplate(fullName, "", new HashMap<>());
	        return jsonProxy.toJson(template);
		} catch (MergeException e) {
			return "NOT FOUND";
		}
    }

    /**********************************************************************************
     * Get a collection of Templates
     *
     * @param list of Collection names
     * @return Json array of Template objects
     */
	public String getTemplatesJSON(List<String> collections) {
    	ArrayList<Template> theList;
        theList = templateCache.getTemplates(collections);
        return jsonProxy.toJson(theList);
    }


    /**********************************************************************************
     * <p>Get a JSON List of Dierctive Types.</p>
     *
     * @return JSON List of Directive Types and Names
     * @see Template
     */
    public String getDirectiveNamesJSON() {
    	Directives directives = new Directives();
        ArrayList<DirectiveName>theList = directives.getDirectiveNames();
        return jsonProxy.toJson(theList);
    }


    /**********************************************************************************
     * <p>Get a JSON List of Collections.</p>
     *
     * @return JSON List of Collection Names
     * @see Template
     */
    public String getCollectionNamesJSON() {
        Set<CollectionName> theList;
        theList = templateCache.getCollectionsFromCache();
        return jsonProxy.toJson(theList);
    }
    public class Name {
		private String collection;
    	public Name(String col) { collection = col; }
    }


    /**********************************************************************************
     * <p>Get a JSON List of Templates in a collection.</p>
     *
     * @param request HttpServletRequest
     * @return JSON List of Collection Names
     * @see Template
     */
    public String getTemplateNamesJSON(String collection) {
        ArrayList<TemplateName> theList;
        theList = templateCache.getTemplateFullnamesFromCache(collection);
        java.util.Collections.sort(theList);
        return jsonProxy.toJson(theList);
    }

    /**********************************************************************************
     * <p>Get a JSON List of Templates in a collection with a given name.</p>
     *
     * @param collection, name
     * @return JSON List of Template Name
     * @see Template
     */
    public String getTemplateNamesJSON(String collection, String name) {
    	ArrayList<TemplateName> theList;
        theList = templateCache.getTemplateFullnamesFromCache(collection, name);
        java.util.Collections.sort(theList);
        return jsonProxy.toJson(theList);
    }
    
    /**********************************************************************************
     * Persist a collection of Templates
     *
     * @param String json array of template json
     */
    @SuppressWarnings("unchecked")
	public String saveTemplatesFromJson(String json) {
    	ArrayList<Template> templates = new ArrayList<Template>();
    	templates = jsonProxy.fromJSON(json, templates.getClass());
    	for (Template template : templates) {
    		Template newTemplate = cache(template);
    		persistence.saveTemplate(newTemplate);
    	}
    	return jsonProxy.toJson(templates);
    }

    
    /**********************************************************************************
     * Persist a template, through the template cache
     *
     * @param String json the template json
     */
    public String saveTemplateFromJson(String json) {
        Template template1 = jsonProxy.fromJSON(json, Template.class);
        Template template = cache(template1);
        persistence.saveTemplate(template);
        return jsonProxy.toJson(template);
    }

    /**********************************************************************************
     * Delete a template from Cache and Persistence
     *
     * @param String json the template json
     */
    public String deleteTemplate(String json) {
        Template template = jsonProxy.fromJSON(json, Template.class);
        templateCache.evict(template.getFullName());
        persistence.deleteTemplate(template);
        return "OK";
    }

    /**********************************************************************************
     * Reset and Reload the cache 
     */
    public void reset() {
        log.warn("Template Cache / Hibernate Reset");
        templateCache.clear();
        loadTemplates();
        log.info("Reset Complete");
    }

    /**********************************************************************************
     * Load all templates from persistence
     */
    public void loadTemplates() {
        List<Template> templates = persistence.loadAllTemplates();
        for (Template t : templates) {
            cache(t);
        }
    }

    /**********************************************************************************
     * Add a template to the cache, and return a cloned copy
     *
     * @param String json the template json
     */
    public Template cache(Template template) {
        templateCache.cache(template.getFullName(), template);
        log.info(template.getFullName() + " has been cached");
        Template clone = templateCache.get(template.getFullName()).clone(new HashMap<>());
        return clone;
    }

   public int size() {
        return templateCache.size();
    }

    public TemplateCache getTemplateCache() {
        return templateCache;
    }

    public AbstractPersistence getPersistence() {
		return persistence;
	}

 	public File getOutputRoot() {
		return outputRoot;
	}
 	   
}