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
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import com.google.gson.Gson;
import com.ibm.util.merge.Template;

/**
 * This class implements a Caching Template Factory as well as all aspects of Template Persistence 
 *
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
	private static final String		FULLNAME_QUERY		= "SELECT ID_TEMPLATE " +
											            "FROM TEMPLATE " +
											            "WHERE TEMPLATE.COLLECTION = :collection " + 
											            "AND TEMPLATE.NAME = :name" + 
											            "AND TEMPLATE.COLUMN = :column";
	private static final String		DEFAULT_QUERY		= "SELECT ID_TEMPLATE" +
													    "FROM TEMPLATE " +
													    "WHERE TEMPLATE.COLLECTION = :collection " + 
													    "AND TEMPLATE.NAME = :name" + 
													    "AND TEMPLATE.COLUMN = ''";
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
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
	 * @throws MergeException 
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
			TemplateFactory.loadAll();
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
	 * @return Template The new Template object
	 * @see Template
	 */
    public static Template getTemplate(String collection, String name, String column, HashMap<String,String> seedReplace) throws MergeException {
    	String fullName = collection + ":" + name + ":" + column;
    	String shortName = collection + ":" + name + ":";
		Template newTemplate = null;
				
    	// Cache hit -- Return a clone of the Template in Cache
    	if ( templateCache.containsKey(fullName) ) { 
        	return templateCache.get(fullName).clone(seedReplace);
    	}
    	
    	// See if FullName template is in the database
        Session session = sessionFactory.openSession();
        @SuppressWarnings({ "rawtypes" })
		List list = session.createQuery(FULLNAME_QUERY).list();
        session.getTransaction().commit();
        session.close();

        if (list.size() == 1) {
            newTemplate = (Template) list.get(0);
    		templateCache.putIfAbsent(fullName, newTemplate);
    		log.info("Constructed Template: " + fullName);
    		return templateCache.get(fullName).clone(seedReplace);
        }

		// Check for shortName in the cache, since fullName doesn't exist
    	if (templateCache.containsKey(shortName)) {
			templateCache.putIfAbsent(fullName, templateCache.get(shortName));
			log.info("Linked Template: " + shortName + " to " + fullName);
			return templateCache.get(fullName).clone(seedReplace);
    	}
    		
    	// See if the shortName (Default Template) is in the database
        session = sessionFactory.openSession();
        list = session.createQuery(DEFAULT_QUERY).list();
        session.getTransaction().commit();
        session.close();

    	if (list.size() == 1) {
			templateCache.putIfAbsent(fullName, newTemplate);
			log.info("Linked Template: " + shortName + " to " + fullName);
			return templateCache.get(fullName).clone(seedReplace);
    	}
    	
    	// Template Not Found Exception
		throw new MergeException("Tempalte and Default Not Found", fullName);
    }
    
    /**********************************************************************************
	 * Set the template folder, and load the cache
	 * @param folder that contains template files
	 */
    public static void loadFolder(String folderName) {
    	TemplateFactory.templateFolder = folderName;
    	TemplateFactory.loadAll();
    }
    
    /**********************************************************************************
	 * Cache JSON templates found in the template folder. 
	 * Note: The template folder is initialized from Merge.java from the web.xml value  for 
	 * merge-templates-folder, if it is not initilized the default value is /tmp/templates
	 * @param folder that contains template files
	 */
    public static void loadAll() {
    	if (TemplateFactory.templateFolder.isEmpty()) { return; }
    	int count = 0;
    	
    	File folder = new File(TemplateFactory.templateFolder);
    	for (File file : folder.listFiles()) {
    		if (!file.isDirectory()) {
    			try {
    				cacheFromJson(new Scanner(file).useDelimiter("\\Z").next());
    			} catch (FileNotFoundException e) {
    				log.info("Moving on after file read error on " + file.getName());
    			}
    		}
    		count++;
    	}
    	log.info("Loaded " + Integer.toString(count) + " templates from " + TemplateFactory.templateFolder);
    }
    
    /**
	 * Construct a template from a json string, add it to the cache and 
	 * save template to the Template database
	 * @param Template JSON
	 * @return
	 */
	public static Template saveTemplate(String jsonString) throws MergeException {
		Template template = cacheFromJson(jsonString);
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save( template );
		session.getTransaction().commit();
		session.close();		
		return template;
	}
	
    /**
	 * Construct a template from a json formated http request and add it to the cache 
	 * @param request
	 * @return the Template created
	 * @throws MergeException - JSON Parsing Errors
	 */
    public static Template cacheFromJson(String jsonString)  {
    	Template template;
		Gson gson = new Gson();
		template = gson.fromJson(jsonString, Template.class);   
		templateCache.remove(template.getFullName());
		templateCache.putIfAbsent(template.getFullName(), template);
		log.info(template.getFullName() + " has been cached");
		return templateCache.get(template.getFullName());
    }
    
    /**********************************************************************************
	 * Reset the cache and Hibernate Connection
	 */
    public static void reset() {
    	log.warn("Template Cache Reset");
    	templateCache.clear();
		log.info("Cache Reset");
    }

    /**********************************************************************************
	 * Initilize the Hibernate Connection
	 */
	public static void initilizeHibernate() {
	    Configuration configuration = new Configuration();
	    configuration.configure();
		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
	            configuration.getProperties()).build();
	    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	public static int size() {
		return templateCache.size();
	}


}