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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.storage.*;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.provider.*;

/**
 * The Class Merger is the primary interface to IDMU. 
 * 
 * @see package-info 
 * @author Mike Storey
 * @since: v4.0
 */
public class Merger {
	// Enumeration Constants
	public static final String DATA_SOURCE			= "DATA_SOURCE";
	public static final String IDMU_PARAMETERS	 	= "idmuParameters";
	public static final String IDMU_PAYLOAD			= "idmuPayload";
	public static final String IDMU_ARCHIVE_TYPE	= "archiveType";
	public static final String IDMU_ARCHIVE_NAME	= "archiveName";
	public static final String IDMU_CONTEXT			= "idmuContext";
	public static final String IDMU_ARCHIVE			= "idmuArchive";
	public static final String IDMU_ARCHIVE_FILES	= "idmuArchiveFiles";
	public static final String IDMU_ARCHIVE_OUTPUT	= "idmuArchiveOutput";
	public static final HashSet<String> DATA_SOURCES() {
		HashSet<String> values = new HashSet<String>();
		values.add(IDMU_PARAMETERS);
		values.add(IDMU_PAYLOAD);
		values.add(IDMU_CONTEXT);
		values.add(IDMU_ARCHIVE);
		values.add(IDMU_ARCHIVE_TYPE);
		values.add(IDMU_ARCHIVE_NAME);
		values.add(IDMU_ARCHIVE_FILES);
		return values;
	}
	
	/**
	 * Enumeration List
	 * @return
	 */
	public static final HashMap<String, HashSet<String>> ENUMS() {
		HashMap<String, HashSet<String>> myEnums = new HashMap<String, HashSet<String>>(); 
		myEnums.put(DATA_SOURCE, DATA_SOURCES());
		return myEnums;
	}

	// Instance Variables
	private Config config;
	private TemplateCache cahce;
	private Template baseTemplate;
	private DataManager mergeData;
	private ArrayList<String> templateStack;
	HashMap<String,ProviderInterface> providers;
	private Archive archive;
	
	/**
	 * Instantiates a new merge context.
	 *
	 * @param cache
	 * @param config
	 * @param template
	 * @throws MergeException
	 */
	public Merger(
			TemplateCache cache, 
			Config config,
			String template) throws MergeException {
		this.cahce = cache;
		this.config = config;
		this.baseTemplate = cache.getMergable(this, template, new HashMap<String,String>());
		this.mergeData = new DataManager();
		this.templateStack = new ArrayList<String>();
		this.mergeData = new DataManager();
		this.providers = new HashMap<String, ProviderInterface>();
	}
	
	/**
	 * Instantiates a new merge context, with initial data values
	 * Convenience method for merging with HTTP request parameters and payload
	 *
	 * @param cache
	 * @param config
	 * @param template
	 * @param parameterMap
	 * @param requestData
	 * @throws MergeException
	 */
	public Merger(
			TemplateCache cache, 
			Config config,
			String template, 
			Map<String,String[]> parameterMap,
			String requestData) throws MergeException {
		this(cache,config,template);
		mergeData.put(Merger.IDMU_PARAMETERS, "-", parameterMap);
		mergeData.put(Merger.IDMU_PAYLOAD, 	  "-", requestData);
	}
	
	/**
	 * Gets the merged template.
	 *
	 * @return template Merged template
	 * @throws MergeException 
	 */
	public Template merge() throws MergeException {
		this.baseTemplate.getMergedOutput();
		return this.baseTemplate;
	}
	
	/**
	 * @param templateName
	 * @param defaultTemplate
	 * @param replace
	 * @return template - Get a mergable template from the cache
	 * @throws MergeException
	 */
	public Template getMergable(String templateName, String defaultTemplate, HashMap<String,String> replace) throws MergeException {
		Template template = cahce.getMergable(this, templateName, defaultTemplate, replace);
		this.pushTemplate(template.getId().shorthand());
		return template;
	}
	
	/**
	 * @return archive The merge output archive
	 * @throws MergeException
	 */
	public Archive getArchive() throws MergeException {
		if (null == this.archive) {
			this.archive = new TarArchive(this);
			if (this.mergeData.contians(Merger.IDMU_PARAMETERS + "-" + Merger.IDMU_ARCHIVE_TYPE, "-")) {
				switch (this.mergeData.get(Merger.IDMU_PARAMETERS + "-" + Merger.IDMU_ARCHIVE_TYPE + "-[0]", "-").getAsPrimitive()  ) {
				case Archive.ARCHIVE_GZIP :
					this.archive = new GzipArchive(this);
					break;
				case Archive.ARCHIVE_TAR :
					this.archive = new TarArchive(this);
					break;
				case Archive.ARCHIVE_ZIP :
					this.archive = new ZipArchive(this);
					break;
				case Archive.ARCHIVE_JAR:
					this.archive = new JarArchive(this);
					break;
				}
			}
			if (this.mergeData.contians(Merger.IDMU_PARAMETERS + "-" + Merger.IDMU_ARCHIVE_NAME, "-")) {
				this.archive.setFileName(mergeData.get(Merger.IDMU_PARAMETERS + "-" + Merger.IDMU_ARCHIVE_NAME + "-[0]", "-").getAsPrimitive());
			}
			this.archive.setFilePath(config.getTempFolder());
			return this.archive;
		}
		return archive;
	}

	/**
	 * @param enrichClass
	 * @param enrichSource
	 * @param dbName
	 * @return provider A Data Provider
	 * @throws MergeException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ProviderInterface getProvider(String enrichClass, String enrichSource, String dbName) throws MergeException {
		if (this.providers.containsKey(enrichSource)) {
			return providers.get(enrichSource);
		}
		
		ProviderInterface theProvider;
		try {
			Class[] cArg = new Class[3]; 
			cArg[0] = String.class;
			cArg[1] = String.class;
			cArg[2] = Merger.class;
			Class clazz = Class.forName(enrichClass);
			theProvider = (ProviderInterface) clazz.getDeclaredConstructor(cArg).newInstance(enrichSource, dbName, this);
		} catch (ClassNotFoundException e1) {
			throw new Merge500("Error finding provider - class not found: " + enrichClass );
		} catch (InstantiationException e) {
			throw new Merge500("Error instantiating class: " + enrichClass + " message: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Merge500("Error accessing class: " + enrichClass + " message: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new Merge500("IllegalArgumentException : " + enrichClass + " message: " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new Merge500("InvocationTargetException: " + enrichClass + " message: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new Merge500("NoSuchMethodException: " + enrichClass + " message: " + e.getMessage());
		} catch (SecurityException e) {
			throw new Merge500("Error accessing class: " + enrichClass + " message: " + e.getMessage());
		}
		providers.put(enrichSource, theProvider);
		return theProvider;
	}

	/**
	 * @return providers
	 */
	public HashMap<String, ProviderInterface> getProviders() {
		return providers;
	}

	/**
	 * @return manager The Data Manager
	 */
	public DataManager getMergeData() {
		return mergeData;
	}

	/**
	 * Clear the merge data object
	 */
	public void clearMergeData() {
		this.mergeData.clear();
		
	}

	/**
	 * The templateStack represents the current insert context and the size indicates the level of sub-templates
	 * @return merging template stack
	 */
	public ArrayList<String> getTemplateStack() {
		return templateStack;
	}

	/**
	 * @return Template Stack Size (nested sub-template level)
	 */
	public int getStackSize() {
		return templateStack.size();
	}
	
	/**
	 * @param name Template Name to push on to insert stack
	 */
	public void pushTemplate(String name) {
		templateStack.add(name);
	}
	
	/**
	 * remove a template from the stack
	 */
	public void popTemplate() {
		if (templateStack.size() > 0) {
			templateStack.remove(templateStack.size()-1);
		}
	}
	

	// Simple Getters below here
	
	/**
	 * @return config The current configuration object
	 */
	public Config getConfig() {
		return config;
	}

	/**
	 * @return cache The Templace Cache
	 */
	public TemplateCache getCahce() {
		return cahce;
	}

	/**
	 * @return template The Base template
	 */
	public Template getBaseTemplate() {
		return baseTemplate;
	}

}
