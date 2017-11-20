/*
 * Copyright 2015-2017 IBM
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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge403;
import com.ibm.util.merge.exception.Merge404;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Stats;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.TemplateId;
import com.ibm.util.merge.template.TemplateList;
import com.ibm.util.merge.template.directive.Enrich;
import com.ibm.util.merge.template.directive.Insert;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.Replace;
import com.ibm.util.merge.template.directive.SaveFile;

/**
 * The Class TemplateCache provides a cache of all templates used in the
 * merge process. The cache has get / put / post / delete method that 
 * should map to a Rest Interface. Initial templates are loaded from the
 * configured Config.loadFolder if it exists. 
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class TemplateCache implements Iterable<String> {
	private static final Logger LOGGER = Logger.getLogger(TemplateCache.class.getName());
	private final HashMap<String, Template> cache;
	private final DataProxyJson gsonProxy = new DataProxyJson();
	
	// Cache Statistics
	private double cacheHits = 0;
	Date initialized = new Date();
    
	/**
	 * Instantiates a new template cache.
	 *
	 * @throws MergeException  on processing errors
	 */
	public TemplateCache() throws MergeException {
		this.cache = new HashMap<String, Template>();
		this.initialized = new Date();
		this.buildDefaultSystemTemplates();
		
		if (!Config.loadFolder().isEmpty()) {
			File templateFolder = new File(Config.loadFolder());
			if (!templateFolder.exists()) {
				LOGGER.log(Level.WARNING, "Template Load Folder not found: " + Config.loadFolder());
				return;
			}
			
			File[] groups = templateFolder.listFiles();
			if (null == groups) {
				LOGGER.log(Level.WARNING, "Template Load Folder is empty: " + Config.loadFolder());
				return;
			}
			
			for (File file : groups) {
				try {
					this.postGroup(new String(Files.readAllBytes(file.toPath()), "ISO-8859-1"));
				} catch (Throwable e) {
					LOGGER.log(Level.WARNING, "Template Group failed to load: " + file.getAbsolutePath());
				}
			}
		}
	}
	
	/**
	 * Build the system default templates (exception handling)
	 * @throws MergeException  on processing errors
	 */
	public void buildDefaultSystemTemplates() {
		// Build Default Templates
		try {
			this.deleteGroup("system");
		} catch (Throwable e) {
			// ignore not found
		}
		
		// Add System Templates
		try {
			Template error403 = new Template("system","error403","","Error - Forbidden");
			Template error404 = new Template("system","error404","","Error - Not Found");
			Template error500 = new Template("system","error500","","Error - Merge Error");
			Template sample = new Template("system","sample","");
			sample.addDirective(new Enrich());
			sample.addDirective(new Insert());
			sample.addDirective(new ParseData());
			sample.addDirective(new Replace());
			sample.addDirective(new SaveFile());
			postTemplate(error403);
			postTemplate(error404);
			postTemplate(error500);
			postTemplate(sample); 
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Load System Templates Failed!" + e.getMessage());
		}
		
	}

	/**
	 * Gets a mergable template - getting the default template if the primary template does not exist.
	 *
	 * @param context The Merge Context
	 * @param templateShortname The Template Name
	 * @param templateDefault The Default template to use if Name not found
	 * @param replace The initial replace stack to be added to the template
	 * @return the Mergable template
	 * @throws MergeException on processing errors
	 */
	public Template getMergable(Merger context, String templateShortname, String templateDefault, HashMap<String,String> replace) throws MergeException {
		if (cache.containsKey(templateShortname)) {
			Template template = cache.get(templateShortname);
			this.cacheHits++;
			return template.getMergable(context, replace);
		} else if (cache.containsKey(templateDefault)) {
			Template template = cache.get(templateDefault);
			this.cacheHits++;
			return template.getMergable(context, replace);
		}
		throw new Merge404("Template not found - " + templateShortname + "-" + templateDefault);
	}

	/**
	 * Gets a mergable template
	 *
	 * @param context The Merge Context
	 * @param templateShortname Template Name
	 * @param replace Replace Stack to initialize
	 * @return the Mergable template
	 * @throws MergeException on processing errors
	 */
	public Template getMergable(Merger context, String templateShortname, HashMap<String,String> replace) throws MergeException {
		if (!cache.containsKey(templateShortname)) {
			throw new Merge404("Template not found:" + templateShortname);
		}
		Template template = cache.get(templateShortname);
		this.cacheHits++;
		return template.getMergable(context, replace);
	}

	/**
	 * Get a mergable template with an empty replace stack
	 * 
	 * @param context The Merge Context
	 * @param templateShortname The template name
	 * @return The Mergable template
	 * @throws MergeException on processing errors
	 */
	public Template getMergable(Merger context, String templateShortname) throws MergeException {
		return getMergable(context, templateShortname, new HashMap<String,String>());
	}

	/**
	 * Post template.
	 *
	 * @param templateJson the template json
	 * @return the string
	 * @throws MergeException on processing errors
	 */
	public String postTemplate(String templateJson) throws MergeException {
		Template template;
		template = gsonProxy.fromString(templateJson, Template.class);
		if (null == template) {
			throw new Merge403("Invalid Json");
		}
		return postTemplate(template);
	}
	
	/**
	 * Post template.
	 *
	 * @param template the template 
	 * @return the string
	 * @throws MergeException  on processing errors
	 */
	public String postTemplate(Template template) throws MergeException {
		String name = template.getId().shorthand();
		if (cache.containsKey(name)) {
			throw new Merge403("Duplicate Found:" + name);
		}
		template.cachePrepare();
		cache.put(name, template);
		return "ok";
	}
	
	/**
	 * Gets the template.
	 *
	 * @param shortHand the Template Name
	 * @return the template
	 */
	public String getTemplate(String shortHand) {
		TemplateId id = new TemplateId(shortHand);
		TemplateList templates = getTemplates(id);
		return gsonProxy.toString(templates);
	}

	/**
	 * Gets the template.
	 *
	 * @param id the template id 
	 * @return the template List
	 */
	public TemplateList getTemplates(TemplateId id) {
		TemplateList templates = new TemplateList();
		for (Template template : cache.values()) {
			if (	(id.group.isEmpty() 	|| id.group.equals(template.getId().group)) &&
					(id.name.isEmpty() 		|| id.name.equals(template.getId().name) ) &&
					(id.variant.isEmpty() 	|| id.variant.equals(template.getId().variant))) {
				templates.add(template);
			}
		}
		return templates;
	}

	/**
	 * Put template.
	 *
	 * @param templateJson the template json
	 * @return the string
	 * @throws MergeException  on processing errors
	 */
	public String putTemplate(String templateJson) throws MergeException {
		Template template = gsonProxy.fromString(templateJson, Template.class);
		if (null == template) {
			throw new Merge403("Invalid Json");
		}
		return putTemplate(template);
	}
	
	/**
	 * Put template.
	 *
	 * @param template the template 
	 * @return the string
	 * @throws MergeException when template not found in cache
	 */
	public String putTemplate(Template template) throws MergeException {
		String name = template.getId().shorthand();
		if (!cache.containsKey(name)) {
			throw new Merge404("Not Found:" + template.getId().shorthand());
		}
		template.cachePrepare();
		cache.put(name, template);
		return "ok";
	}
	
	/**
	 * Delete template.
	 *
	 * @param shorthand the template id 
	 * @return the string
	 * @throws MergeException  on processing errors
	 */
	public String deleteTemplate(String shorthand) throws MergeException {
		TemplateId id = new TemplateId(shorthand);
		deleteTemplate(id);
		return "ok";
	}
	
	/**
	 * Delete template.
	 *
	 * @param id The template id 
	 * @return The success message
	 * @throws MergeException  on processing errors
	 */
	public String deleteTemplate(TemplateId id) throws MergeException {
		if (!cache.containsKey(id.shorthand())) {
			throw new Merge403("Not Found:" + id.shorthand());
		}
		cache.remove(id.shorthand());
		return "ok";
	}
	
	/**
	 * Gets the list of template groups.
	 *
	 * @return the group
	 */
	public HashSet<String> getGroupList() {
		HashSet<String> groups = new HashSet<String>();
		for (Template template : cache.values()) {
			groups.add(template.getId().group);
		}
		return groups;
	}

	/**
	 * Post group.
	 *
	 * @param groupJson the group json
	 * @return the string
	 * @throws MergeException  on processing errors
	 */
	public String postGroup(String groupJson) throws MergeException {
		TemplateList templates = gsonProxy.fromString(groupJson, TemplateList.class);
		if (null == templates) {
			throw new Merge403("Invalid Json");
		}

		String group = templates.get(0).getId().group;
		if (getGroupList().contains(group)) {
			throw new Merge403("Duplicate Found:" + group);
		}
		for (Template template : templates) {
			if (template.getId().group.equals(group)) {
				this.postTemplate(template);
			} else {
				throw new Merge403("Invalid Group - multi-group:" + group + ":" + template.getId().group);
			}
		}
		return "ok";
	}
	
	/**
	 * Gets the group.
	 *
	 * @param groupName the group name
	 * @return the group
	 */
	public String getGroup(String groupName) {
		if (groupName.isEmpty()) {
			return this.gsonProxy.toString(getGroupList());
		} 
		TemplateList group = new TemplateList();
		for (Template template : cache.values()) {
			if (template.getId().group.equals(groupName)) {
				group.add(template);
			}
		}
		return gsonProxy.toString(group);
	}

	/**
	 * Put group.
	 *
	 * @param groupJson the group json
	 * @return the string
	 * @throws MergeException  on processing errors
	 */
	public String putGroup(String groupJson) throws MergeException {
		TemplateList templates = gsonProxy.fromString(groupJson, TemplateList.class);
		if (null == templates) {
			throw new Merge403("Invalid Json");
		}
		String groupName = templates.get(0).getId().group;
		
		if (!this.getGroupList().contains(groupName)) {
			throw new Merge403("Not Found:" + groupName);
		}

		deleteTheGroup(groupName);

		for (Template template : templates) {
			this.postTemplate(template);
		}
		return "ok";
	}
	
	/**
	 * Delete group.
	 *
	 * @param groupName the group name
	 * @return the string
	 * @throws MergeException  on processing errors
	 */
	public String deleteGroup(String groupName) throws MergeException {
		if (groupName.equals("system")) {
			throw new Merge403("Forbidden:" + groupName);
		}

		if (!this.getGroupList().contains(groupName)) {
			throw new Merge403("Not Found:" + groupName);
		}
	
		deleteTheGroup(groupName);
		return "ok";
	}

	/**
	 * Gets the list of template groups and template names.
	 *
	 * @return JSON String
	 */
	public String getGroupAndTemplateList() {
		HashMap<String, ArrayList<String>> theTemplates = new HashMap<String, ArrayList<String>>();
		for (Template template : cache.values()) {
			if (!theTemplates.containsKey(template.getId().group)) {
				theTemplates.put(template.getId().group, new ArrayList<String>());
			}
			theTemplates.get(template.getId().group).add(template.getId().shorthand());
		}
		return gsonProxy.toString(theTemplates);
	}

	/**
	 * Update cached template statistics - NOT SYNCRONIZED Subject to inaccuracy 
	 * @param template The template shortname to update
	 * @param response The response time of merging the template
	 */
	public void postStats(String template, Long response) {
		if (this.contains(template)) {
			this.cache.get(template).postStats(response);
		}
	}
	
	/**
	 * @return template statistics
	 */
	public Stats getStats() {
		Stats stats = new Stats();
		for (String name : cache.keySet()) {
			stats.add(cache.get(name).getStats());
		}
		return stats;
	}
	
	/**
	 * @return number of templates in cache
	 */
	public int getSize() {
		return this.cache.size();
	}

	/**
	 * @param key Template ID
	 * @return if cache contains a template
	 */
	public boolean contains(String key) {
		return this.cache.containsKey(key);
	}

	/**
	 * @return number of cache hits since instantiation
	 */
	public double getCacheHits() {
		return this.cacheHits;
	}

	/**
	 * @return the date/time the cache was initialized
	 */
	public Date getInitialized() {
		return initialized;
	}

	@Override
	public Iterator<String> iterator() {
		return cache.keySet().iterator();
	}

	/**
	 * Delete the group
	 * @param groupName
	 */
	private void deleteTheGroup(String groupName) {
		HashSet<String> names = new HashSet<String>();
		for (String name : cache.keySet()) {
			if (cache.get(name).getId().group.equals(groupName)) {
				names.add(name);
			}
		}
		
		for (String name : names) {
			cache.remove(name);
		}
	}
	
}
