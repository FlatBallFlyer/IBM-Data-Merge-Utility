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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
 * should map to a Rest Interface.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class TemplateCache implements Iterable<String> {
	private final HashMap<String, Template> cache;
	private final DataProxyJson gsonProxy = new DataProxyJson();
	private final Config config;
	
	// Cache Statistics
	private double cacheHits = 0;
	Date initialized = new Date();
    
	/**
	 * Instantiates a new template cache.
	 *
	 * @param persist the persist
	 * @throws MergeException 
	 */
	public TemplateCache(Config config) throws MergeException {
		this.config = config;
		this.cache = new HashMap<String, Template>();
		this.initialized = new Date();
		 
		// Build Default Templates
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

	}
	
	/**
	 * Gets a mergable template - getting the default template if the primary template does not exist.
	 *
	 * @param id the id
	 * @param replace the replace
	 * @return the mergable
	 * @throws MergeException 
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
	 * @param id the id
	 * @param replace the replace
	 * @return the mergable
	 * @throws MergeException 
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
	 * @param context
	 * @param templateShortname
	 * @return
	 * @throws MergeException
	 */
	public Template getMergable(Merger context, String templateShortname) throws MergeException {
		return getMergable(context, templateShortname, new HashMap<String,String>());
	}

	/**
	 * Post template.
	 *
	 * @param templateJson the template json
	 * @return the string
	 */
	public String postTemplate(String templateJson) throws MergeException {
		Template template;
		template = gsonProxy.fromJSON(templateJson, Template.class);
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
	 * @throws MergeException 
	 */
	public String postTemplate(Template template) throws MergeException {
		String name = template.getId().shorthand();
		if (cache.containsKey(name)) {
			throw new Merge403("Duplicate Found:" + name);
		}
		template.initStats();
		cache.put(name, template);
		return "ok";
	}
	
	/**
	 * Gets the template.
	 *
	 * @param templateIdJson the template id 
	 * @return the template
	 */
	public String getTemplate(String shortHand) {
		TemplateId id = new TemplateId(shortHand);
		TemplateList templates = getTemplates(id);
		return gsonProxy.toJson(templates);
	}

	/**
	 * Gets the template.
	 *
	 * @param templateId the template id 
	 * @return the template
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
	 * @throws MergeException 
	 */
	public String putTemplate(String templateJson) throws MergeException {
		Template template = gsonProxy.fromJSON(templateJson, Template.class);
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
	 * @throws Merge404 
	 */
	public String putTemplate(Template template) throws MergeException {
		String name = template.getId().shorthand();
		if (!cache.containsKey(name)) {
			throw new Merge404("Not Found:" + template.getId().shorthand());
		}
		template.initStats();
		cache.put(name, template);
		return "ok";
	}
	
	/**
	 * Delete template.
	 *
	 * @param templateIdJson the template id 
	 * @return the string
	 */
	public String deleteTemplate(String shorthand) throws MergeException {
		TemplateId id = new TemplateId(shorthand);
		deleteTemplate(id);
		return "ok";
	}
	
	/**
	 * Delete template.
	 *
	 * @param templateIdJson the template id 
	 * @return the string
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
	 * @throws MergeException 
	 */
	public String postGroup(String groupJson) throws MergeException {
		TemplateList templates = gsonProxy.fromJSON(groupJson, TemplateList.class);
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
			return this.gsonProxy.toJson(getGroupList());
		} 
		TemplateList group = new TemplateList();
		for (Template template : cache.values()) {
			if (template.getId().group.equals(groupName)) {
				group.add(template);
			}
		}
		return gsonProxy.toJson(group);
	}

	/**
	 * Put group.
	 *
	 * @param groupJson the group json
	 * @return the string
	 * @throws MergeException 
	 */
	public String putGroup(String groupJson) throws MergeException {
		TemplateList templates = gsonProxy.fromJSON(groupJson, TemplateList.class);
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
	 * @throws MergeException 
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
	 * @return the config being used by the cache;
	 */
	public Config getConfig() {
		return config;
	}
	
	/**
	 * @param key
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
