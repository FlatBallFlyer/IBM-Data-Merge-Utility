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

package com.ibm.dragonfly;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.ibm.dragonfly.Template;

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
	private static final String		KEY_CACHE_RESET		= "CacheReset";
	private static final String		KEY_COLLECTION		= Template.wrap("collection");
	private static final String		KEY_NAME			= Template.wrap("name");
	private static final String		KEY_COLUMN			= Template.wrap("column");
	private static final String		DEFAULT_COLLECETION	= "root";
	private static final String 	DEFAULT_NAME		= "default";
	private static final String		DEFAULT_COLUMN		= "";
	private static final HashMap<String,Template> templateCache = new HashMap<String,Template>();
	
	/**********************************************************************************
	 * <p>Template from Servlet request Constructor. Initiates a template based on http Servlet Request
	 * parameters. Servlet request parameters are used to initilize the Replace hash. The
	 * template to be used is specified on the KEY_COLLECTION, KEY_NAME and KEY_COLUMN parameters.
	 * If no template is provided the DEFAULT_* values are used. The KEY_CACHE_RESET parameter with a 
	 * value of Yes will reset the Template Cache.</p>
	 *
	 * @param  request HttpServletRequest
	 * @throws DragonFlyException Invalid Directive Type
	 * @throws DragonFlySqlException Template Datasource errors
	 * @return Template The new Template object
	 * @see Template
	 */
	public static Template getTemplate(HttpServletRequest request) throws DragonFlySqlException, DragonFlyException {
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
			if (KEY_CACHE_RESET.equals(paramName) && "Yes".equals(paramValue) ) {
				TemplateFactory.reset();
			} else {
				replace.put(Template.wrap(paramName), paramValue);
			}
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
	 * @throws DragonFlyException Invalid Directive Type from Constructor
	 * @throws DragonFlySqlException Template Constructor Errors
	 * @return Template The new Template object
	 * @see Template
	 */
    public static Template getTemplate(String collection, String column, String name, HashMap<String,String> seedReplace) throws DragonFlySqlException, DragonFlyException {
    	String fullName = collection + ":" + column + ":" + name;
    	if ( !templateCache.containsKey(fullName) ) { 
    		Template newTemplate = new Template(collection, column, name);
    		templateCache.put(fullName, newTemplate);
    	}
    	return new Template(templateCache.get(fullName),seedReplace);
    }
    
    /**********************************************************************************
	 * <p>Reset the cache</p>
	 *
	 */
    public static void reset() {
    	templateCache.clear();
    }

}