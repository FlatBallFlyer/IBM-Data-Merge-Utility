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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.cedarsoftware.util.io.JsonReader;
import com.ibm.util.merge.Template;

/**
 * Caching Template Facotory 
 *
 * @see #getTemplate(HttpServletRequest)
 * @see #getTemplate(String, String, String, HashMap)
 * @see Template
 * @author  Mike Storey
 */
final public class TemplateFactory {
	// Factory Constants
	private static final Logger 	log = Logger.getLogger( TemplateFactory.class.getName() );
	private static final String		KEY_CACHE_RESET		= Template.wrap("DragonFlyCacheReset");
	private static final String		KEY_CACHE_LOAD		= Template.wrap("DragonFlyCacheLoad");
	private static final String		KEY_COLLECTION		= Template.wrap("collection");
	private static final String		KEY_NAME			= Template.wrap("name");
	private static final String		KEY_COLUMN			= Template.wrap("column");
	private static final String		DEFAULT_COLLECETION	= "root";
	private static final String 	DEFAULT_NAME		= "default";
	private static final String		DEFAULT_COLUMN		= "";
	private static final ConcurrentHashMap<String,Template> templateCache = new ConcurrentHashMap<String,Template>();
	private static String templateFolder = "/tmp/templates";
	
	/**********************************************************************************
	 * <p>Template from Servlet request Constructor. Initiates a template based on http Servlet Request
	 * parameters. Servlet request parameters are used to initilize the Replace hash. The
	 * template to be used is specified on the KEY_COLLECTION, KEY_NAME and KEY_COLUMN parameters.
	 * If no template is provided the DEFAULT_* values are used. The KEY_CACHE_RESET parameter will 
	 * reset the Template Cache.</p>
	 *
	 * @param  request HttpServletRequest
	 * @throws MergeException Invalid Directive Type
	 * @throws DragonFlySqlException Template Datasource errors
	 * @return Template The new Template object
	 * @see Template
	 */
	public static Template getTemplate(HttpServletRequest request) throws MergeException {
		HashMap<String,String> replace = new HashMap<String,String>();
		// Open the "Default" template if if not specified. 
		replace.put(KEY_COLLECTION, DEFAULT_COLLECETION);
		replace.put(KEY_COLUMN,		DEFAULT_COLUMN);
		replace.put(KEY_NAME, 		DEFAULT_NAME);

		// Iterate parameters, setting replace values 
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String paramValue = request.getParameterValues(paramName)[0];
			replace.put(Template.wrap(paramName), paramValue);
		}
		
		// Handle cache reset request
		if ( replace.containsKey(KEY_CACHE_RESET) ) {
			TemplateFactory.reset();
		} 
		
		// Handle cache load request
		if ( replace.containsKey(KEY_CACHE_LOAD) ) {
			TemplateFactory.load();
		} 
		
		// Get the template, add the http parameter replace values to it's hash
		Template rootTempalte = TemplateFactory.getTemplate(replace.get(KEY_COLLECTION), 
													replace.get(KEY_COLUMN), 
													replace.get(KEY_NAME),
													replace);
		return rootTempalte;
	}
    
    /**********************************************************************************
	 * Get a copy of a cached Template based on a Template Fullname, which is comprised of
	 * collection, name and columnValue (a unique key to the table dragonfly.template) The 
	 * provided Replace hash is copied to the new Template before it is returned
	 *
	 * @param  collection Collection Name
	 * @param  column Column Value
	 * @param  name Template Name
	 * @param  seedReplace Initial replace hash
	 * @throws MergeException Invalid Directive Type from Constructor
	 * @throws DragonFlySqlException Template Constructor Errors
	 * @return Template The new Template object
	 * @see Template
	 */
    public static Template getTemplate(String collection, String column, String name, HashMap<String,String> seedReplace) throws MergeException {
    	// TODO - Consider full name change collection + name + column
    	String fullName = collection + ":" + column + ":" + name;
    	if ( !templateCache.containsKey(fullName) ) { 
    		// TODO - Shift "Default" template logic from constructor to here
    		Template newTemplate = new Template(collection, column, name);
    		templateCache.putIfAbsent(fullName, newTemplate);
    		log.info("Constructed Template: " + fullName);
    	}
    	return templateCache.get(fullName).clone(seedReplace);
    }
    
    /**********************************************************************************
	 * Reset the cache
	 */
    public static void reset() {
    	log.warn("Template Cache Reset");
    	templateCache.clear();
		log.info("Cache Reset");
    }

    /**********************************************************************************
	 * Set the template folder, and load the cache
	 * @param folder that contains template files
	 */
    public static void load(String folderName) {
    	TemplateFactory.templateFolder = folderName;
    	TemplateFactory.load();
    }
    
    /**********************************************************************************
	 * Cache JSON templates found in the template folder. 
	 * Note: The template folder is initialized from Merge.java from the web.xml value  for 
	 * merge-templates-folder, if it is not initilized the default value is /tmp/templates
	 * @param folder that contains template files
	 */
    public static void load() {
    	if (TemplateFactory.templateFolder.isEmpty()) { return; }
    	JsonReader reader = null;
    	Template template = null;
    	int count = 0;
    	
    	File folder = new File(TemplateFactory.templateFolder);
    	for (File file : folder.listFiles()) {
    		if (!file.isDirectory()) {
    			try {
					reader = new JsonReader(new FileInputStream(file));
					template = (Template) reader.readObject();
	    			templateCache.put(template.getFullName(), template);
				} catch (FileNotFoundException e) {
					log.info("Moving on after file read error on " + file.getName());
				}
    		}
    		log.info(template.getFullName() + " has been cached");
    		count++;
    	}
    	log.info("Loaded " + Integer.toString(count) + " templates from " + TemplateFactory.templateFolder);
    }

    
	/**
	 * Construct a template from a json formated http request, add it to the cache and 
	 * save template to the Template database
	 * @param request
	 * @return
	 */
	public static Template addTemplate(HttpServletRequest request) throws MergeException {
		Template template = (Template) JsonReader.jsonToJava(request.toString());
		template.saveToDb();
		templateCache.remove(template.getFullName());
		templateCache.putIfAbsent(template.getFullName(), template);
		return template;
	}

}