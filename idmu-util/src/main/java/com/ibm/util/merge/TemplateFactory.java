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
import com.ibm.idmu.api.PoolManagerConfiguration;
import com.ibm.util.merge.cache.TemplateCache;
import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.directive.AbstractDirective;
import com.ibm.util.merge.directive.Directives;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;
import com.ibm.util.merge.persistence.JdbcTemplatePersistence;
import com.ibm.util.merge.persistence.TemplatePersistence;
import com.ibm.util.merge.template.CollectionName;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.TemplateList;
import com.ibm.util.merge.template.TemplateName;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;

/**
 * <p>This class implements the public interface for IDMU.</p> 
 * 
 * <h3>Usage:</h3>
 *  
 * <h4>To Initialize the Factory</h4>
 * 
 * <h4>To Use the factory to merge a template.</h4>
 * 
 * @see #getMergeOutput(Map)
 * 
 */
final public class TemplateFactory {
    // Factory Constants
    private static final Logger log = Logger.getLogger(TemplateFactory.class.getName());
    public static final String KEY_CACHE_RESET = Template.wrap("DragonFlyCacheReset");
    public static final String KEY_FULLNAME = Template.wrap("DragonFlyFullName");
    public static final String KEY_SHORTNAME = Template.wrap("DragonFlyShortName");
    public static final String DEFAULT_FULLNAME = "system.default.";
    public static final String DEFAULT_SHORTNAME = "system.default.";
    public static final String SYSTEM_STATUS_PAGE = "system.status.";
    
    // Initialization Property Names
    public static final String PARAMETER_TEMPLATE_DIR 		= "merge-templates-folder";
	public static final String PARAMETER_OUTPUT_DIR 		= "merge-output-folder";
	public static final String PARAMETER_PACKAGE_DIR 		= "merge-packages-folder";
	public static final String PARAMETER_POOLS_PROPERTIES 	= "jdbc-pools-properties";
	public static final String PARAMETER_TEMPLATE_POOL 		= "jdbc-persistence-templates-poolname";
	public static final String PARAMETER_TEMPLATE_SCHEMA 	= "jdbc-persistence-templates-schema";
	public static final String PARAMETER_DB_PERSIST 		= "db-persist";
	public static final String PARAMETER_PRETTY_JSON 		= "pretty-json";

	// Factory Attributes
	private final String idmu_version = "3.1.4";
	private final Properties runtimeProperties;
    private final TemplatePersistence persistence;
    private final File outputRoot;
    private final File packageFolder;
	private final TemplateCache templateCache;
    private final JsonProxy jsonProxy;
    private final ConnectionPoolManager poolManager;
    
    // Factory Counters
    private double mergeCount = 0;
    private double mergeTime = 0;
    private Date initialized = null;
    private double templatesMerged = 0;
    
    public TemplateFactory(Properties runtimeProperties) {
    	this.runtimeProperties = runtimeProperties;
        this.templateCache = new TemplateCache();
        this.poolManager = new ConnectionPoolManager();
        this.jsonProxy = (runtimeProperties.getProperty(PARAMETER_PRETTY_JSON).equals("yes") ? new PrettyJsonProxy() : new DefaultJsonProxy());
        this.outputRoot = new File(runtimeProperties.getProperty(PARAMETER_OUTPUT_DIR));
        this.packageFolder = new File(runtimeProperties.getProperty(PARAMETER_PACKAGE_DIR));
        File poolsPropertiesPath = new File(runtimeProperties.getProperty(PARAMETER_POOLS_PROPERTIES));
        if(poolsPropertiesPath.exists()){
            PoolManagerConfiguration config = PoolManagerConfiguration.fromPropertiesFile(poolsPropertiesPath);
            poolManager.applyConfig(config);
        }else{
            log.error("Could not load databasePools properties file from non-existant path: " + poolsPropertiesPath);
            log.error("No database config will be applied");
        }

        if(runtimeProperties.getProperty(PARAMETER_DB_PERSIST).equals("yes")){
            JdbcTemplatePersistence jdbcPersistence = new JdbcTemplatePersistence(poolManager);
            jdbcPersistence.setPoolName(runtimeProperties.getProperty(PARAMETER_TEMPLATE_POOL));
            jdbcPersistence.setSchemaName(runtimeProperties.getProperty(PARAMETER_TEMPLATE_SCHEMA));
            this.persistence = jdbcPersistence;
        }else{
            this.persistence = new FilesystemPersistence(
            		new File(runtimeProperties.getProperty(PARAMETER_TEMPLATE_DIR)), 
            		this.jsonProxy);
        }
        reset();
    }
    
    /**********************************************************************************
     * <p>Call this method Merge a template with it's sub-templates and data.</p>
     * <p>The request parameters list contains the initial replace values for the merge.
     * The following examples show the use of special parameters
     * 
     * <pre>
	 * {@code
	 * TemplateFactory.KEY_FULLNAME specifies the fullname (collection.name.column) of the Template to merge.
	 *      parameterMap.put(TemplateFactory.KEY_FULLNAME, new String[]{"root.test."});
	 *      The full name will always have two delimiters - note the trailing period.
     * TemplateFactory.KEY_CACHE_RESET will clear and reload the template cache - use after updating templates
	 * Template.TAG_OUTPUT_TYPE = Template.TYPE_ZIP to create a zip archive instead of the default TYPE_TAR archive.
	 * Template.TAG_OUTPUTFILE to override the GUID generated output file name 
	 *      Use at your own risk, concurrent merges with the same output file will fail with unpredictable results.
	 * Template.TAG_SOFTFAIL to enable "Soft Fail" for the merge 
	 *      (merge exceptions are written to output allowing merge to continue, useful for testing)
	 * }
	 * </pre>
     *
     * @param requestParameters a request parameters map of String, String[] i.e. ServletRequest.parameters
     * @return String the Merged Output
     * @throws MergeException 
     * @see Template
     */
    public String getMergeOutput(Map<String, String[]> requestParameters) throws MergeException {
        double begin = System.currentTimeMillis();
        HashMap<String, String> replace = new HashMap<>();
        
        // Open the "Default" template if if not specified.
        replace.put(KEY_FULLNAME,  DEFAULT_FULLNAME);
        replace.put(KEY_SHORTNAME, DEFAULT_SHORTNAME);
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
        String fullName = replace.get(KEY_FULLNAME);
        String shortName = replace.get(KEY_SHORTNAME);
        log.info("Merging TEMPLATE fullName " + fullName + " shortName " + shortName);
        Template rootTemplate = getMergableTemplate(fullName, shortName, replace);
        String returnValue = rootTemplate.getMergedOutput(rtc);
        try {
			rtc.finalize();
		} catch (Exception e) {
			log.error("Context Finalize Error:" + e.getLocalizedMessage());
		}
        this.mergeCount++;
        this.mergeTime += System.currentTimeMillis() - begin;
        return returnValue;
    }

    /**********************************************************************************
     * Get a mergable copy of a cached Template based desired and default template name
     *
     * @param fullName Template full name
     * @param shortName "Default" template full name (if full name not found)
     * @param seedReplace Initial replace hash
     * @return Template The new Template object
     * @throws MergeException when the template is not found or can not be merged
     */
    public Template getMergableTemplate(String fullName, String shortName, Map<String, String> seedReplace) throws MergeException {
        Template newTemplate = null;
        // Cache hit -- Return a clone of the Template in Cache
        if (templateCache.isCached(fullName)) {
            newTemplate = templateCache.get(fullName).getMergable(seedReplace);
        }
        // Check for shortName in the cache, since fullName doesn't exist
        if (newTemplate == null && templateCache.isCached(shortName)) {
            newTemplate = templateCache.get(shortName).getMergable(seedReplace);
        }
        if (newTemplate == null) {
            throw new MergeException("Template Not Found", fullName + " and " + shortName, seedReplace);
        }
        this.templatesMerged++;
        return newTemplate;
    }

    /**********************************************************************************
     * Get a template as jSon from cache
     *
     * @param fullName the Template Full Name
     * @return String a single template JSON string or "NOT FOUND"
     * @throws MergeException 
     */
    public String getTemplateAsJson(String fullName) throws MergeException {
        Template template = getMergableTemplate(fullName, "", new HashMap<String,String>());
        return jsonProxy.toJson(template);
    }

    /**********************************************************************************
     * Get a collection of Templates
     *
     * @param collections of Collection names
     * @return Json array of Template objects
     */
	public String getTemplatesJSON(List<String> collections) {
    	TemplateList theList;
        theList = templateCache.getTemplates(collections);
        return jsonProxy.toJson(theList);
    }


    /**********************************************************************************
     * <p>Get a JSON List of Dierctive Types.</p>
     *
     * @return JSON List of Directive JSON objects for all supported directives
     * @see Directives
     */
    public String getDirectiveNamesJSON() {
    	Directives directives = new Directives();
        ArrayList<AbstractDirective>theList = directives.getDirectives();
        return jsonProxy.toJson(theList);
    }


    /**********************************************************************************
     * <p>Get a JSON List of Collections.</p>
     *
     * @return JSON List of Collection Names in the form ["collection":"root","collection":"test"...]
     */
    public String getCollectionNamesJSON() {
        ArrayList<CollectionName> theList;
        theList = templateCache.getCollectionsFromCache();
        java.util.Collections.sort(theList);
        return jsonProxy.toJson(theList);
    }

    /**********************************************************************************
     * <p>Get a JSON List of Template Names for a collection.</p>
     *
     * @param collection collection name
     * @return JSON List of template names
     * 	in the form [{"collection":"root","name":"default","columnValue":""},...]
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
     * @param collection the Collection Name
     * @param name the Tempalte Name
     * @return JSON List of Template Name
     * 	in the form [{"collection":"root","name":"default","columnValue":""},...]
     * @see Template
     */
    public String getTemplateNamesJSON(String collection, String name) {
    	ArrayList<TemplateName> theList;
        theList = templateCache.getTemplateFullnamesFromCache(collection, name);
        java.util.Collections.sort(theList);
        return jsonProxy.toJson(theList);
    }
    
    /**********************************************************************************
     * Persist a collection of Templates from the packages folder
     *
     * @param File name (in the packages folder)
     * @return OK or ERROR
     * @throws MergeException 
     */
	public String loadPackage(String fileName) throws MergeException {
		log.info("Starting loadPackage");
		File thePackage = new File(this.packageFolder + File.separator +  fileName);
		if (!thePackage.exists()) {
			throw new MergeException("File not found while trying to load a package", thePackage.getAbsolutePath(), null);
		}
		String templateJson;
		try {
			templateJson = new String(Files.readAllBytes(thePackage.toPath()));
		} catch (IOException e) {
			throw new MergeException(e, "ERROR", "File IO Exception reading " + fileName, null);
		}
		String result = this.saveTemplatesFromJson(templateJson);
		return result;
    }

    /**********************************************************************************
     * Persist a collection of Templates
     *
     * @param json array of template json
     * @return the saved templates json array
     */
	public String saveTemplatesFromJson(String json) {
		log.info("Starting saveTemplatesFromJson");
		TemplateList templates = new TemplateList();
    	templates = jsonProxy.fromJSON(json, templates.getClass());
    	for (Template template : templates.templates) {
    		cache(template);
			persistence.saveTemplate(template.getMergable(new HashMap<String,String>()));
    	}
    	return jsonProxy.toJson(templates);
    }
    
    /**********************************************************************************
     * Persist a template, through the template cache
     *
     * @param json the template json
     * @return the saved template json string, FORBIDDEN for invalid template name or FAIL if an error occurred.
     * @throws MergeException 
     */
    public String saveTemplateFromJson(String json) throws MergeException {
        Template template = jsonProxy.fromJSON(json, Template.class);
        if (template.getFullName().equals("..")) {
        	throw new MergeException("FORBIDDEN", "Empty Template Name", null);
        }
        cache(template);
        persistence.saveTemplate(template.getMergable(new HashMap<String,String>()));
        return jsonProxy.toJson(template);
    }

    /**********************************************************************************
     * Delete a template from Cache and Persistence
     *
     * @param fullName the template fullname
     * @return "OK" or "FORBIDDEN"
     * @throws MergeException 
     */
    public String deleteTemplate(String fullName) throws MergeException {
        Template template = this.getMergableTemplate(fullName, "", new HashMap<String,String>());
        templateCache.evict(template.getFullName());
        persistence.deleteTemplate(template);
        return "OK";
    }

    /**********************************************************************************
     * Delete a collection of template from Cache and Persistence
     *
     * @param List of collection names
     * @return "OK" or "FORBIDDEN"
     * @throws MergeException 
     */
    public String deleteCollections(List<String> collections) throws MergeException {
        TemplateList templates = this.templateCache.getTemplates(collections);
        for (Template template : templates.templates) {
        	this.deleteTemplate(template.getFullName());
        }
        return "OK";
    }

    /**********************************************************************************
     * Get the TemplateFactory status page
     *
     * @return the HTML Status paged as merged from TemplateFactory.SYSTEM_STATUS_PAGE
     * @throws MergeException 
     */
    public String getStatusPage() throws MergeException {
    	HashMap<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("DragonFlyFullName", 	new String[]{TemplateFactory.SYSTEM_STATUS_PAGE});
		parameterMap.put("MERGE_COUNT", 		new String[]{Double.toString(mergeCount)});
		parameterMap.put("MERGE_TIME", 			new String[]{Double.toString(mergeTime)});
		parameterMap.put("MERGE_AVG", 			new String[]{Double.toString(mergeTime/mergeCount)});
		parameterMap.put("TEMPLATES_MERGED", 	new String[]{Double.toString(templatesMerged)});
		parameterMap.put("LAST_RESET", 			new String[]{this.initialized.toString()});
		parameterMap.put("TEMPLATES_CACHED", 	new String[]{Integer.toString(this.templateCache.size())});
		parameterMap.put("IDMU_VERSION", 		new String[]{this.idmu_version});
		parameterMap.put("CACHE_STATS", 		new String[]{this.templateCache.cacheStatus()});
		for (Entry<Object, Object> e : this.runtimeProperties.entrySet()) {
			parameterMap.put((String) e.getKey(), new String[]{(String) e.getValue()});
		}
		return this.getMergeOutput(parameterMap);
    }

    /**********************************************************************************
     * Reset and Reload the cache 
     */
    public void reset() {
        log.warn("Template Cache / Hibernate Reset");
        templateCache.clear();
        loadTemplates();
        log.info("Reset Complete");
        initialized = new Date();
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
     * @param template the template json
     */
    public void cache(Template template) {
    	template.setMergable(false);
        templateCache.cache(template.getFullName(), template);
        log.info(template.getFullName() + " has been cached");
    }
    
    /**
     * @return cache size
     */
    public int size() {
        return templateCache.size();
    }

    /**
     * @return the pool manager
     */
    public ConnectionPoolManager getPoolManager() {
		return poolManager;
	}

	/**
	 * @return the template cache
	 */
	public TemplateCache getTemplateCache() {
        return templateCache;
    }

    /**
     * @return the persistence object
     */
    public TemplatePersistence getPersistence() {
		return persistence;
	}

 	/**
 	 * @return the output root folder
 	 */
 	public File getOutputRoot() {
		return outputRoot;
	}
 	   
}