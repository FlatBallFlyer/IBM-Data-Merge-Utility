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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.storage.*;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.Enrich;
import com.ibm.util.merge.template.directive.enrich.provider.*;

/**
 * <p>The Class Merger provides context to the merge process and hosts the 
 * data manager and provider instances for the merge process.</p>
 * <ul>	<li>Config: {@link com.ibm.util.merge.Config}</li>
 * 		<li>Template: {@link com.ibm.util.merge.template.Template}</li>
 * 		<li>Providers: {@link com.ibm.util.merge.template.directive.enrich.provider.ProviderInterface}</li>
 *  </ul>
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Merger {
	private Cache cache;
	private Config config;
	private Template baseTemplate;
	private DataManager mergeData;
	private ArrayList<String> templateStack;
	HashMap<String,ProviderInterface> providers;
	private Archive archive;
	
	/**
	 * Instantiates a new merge context.
	 *
	 * @param cache The cache to use when merging
	 * @param template The root template to merge
	 * @throws MergeException on processing errors
	 */
	public Merger(
			Cache cache, 
			String template) throws MergeException {
		this.cache = cache;
		this.config = cache.getConfig();
		this.baseTemplate = cache.getMergable(this, template, new HashMap<String,String>());
		this.mergeData = new DataManager();
		this.templateStack = new ArrayList<String>();
		this.mergeData = new DataManager();
		this.providers = new HashMap<String, ProviderInterface>();
	}
	
	/**
	 * @param cache The cache to use when merging
	 * @param template The root template to merge
	 * @param parameterMap The parameters to preload in the data manager
	 * @param payload The input stream for the request payload
	 * @param encoding The encoding of the input stream
	 * @throws MergeException on bad parameters
	 */
	public Merger(
			Cache cache, 
			String template, 
			Map<String,String[]> parameterMap,
			InputStream payload,
			String encoding) throws MergeException {
		this(cache,template);
		mergeData.put(Merger.IDMU_PARAMETERS, "-", parameterMap);
		String requestData;
		try {
			requestData = IOUtils.toString(payload, encoding);
		} catch (IOException e) {
			throw new Merge500("Error - invalid encoding when constructing Merger");
		}
		mergeData.put(Merger.IDMU_PAYLOAD, 	  "-", requestData);
	}
	
	/**
	 * Instantiates a new merge context, with initial data values
	 * Convenience method for merging with HTTP request parameters and payload
	 *
	 * @param cache The cache to use
	 * @param template The template name
	 * @param parameterMap A Parameter map to load in merge data
	 * @param requestData A request payload to load in merge data
	 * @throws MergeException on processing errors
	 */
	public Merger(
			Cache cache, 
			String template, 
			Map<String,String[]> parameterMap,
			String requestData) throws MergeException {
		this(cache,template);
		mergeData.put(Merger.IDMU_PARAMETERS, "-", parameterMap);
		mergeData.put(Merger.IDMU_PAYLOAD, 	  "-", requestData);
	}
	
	/**
	 * Gets the merged template.
	 *
	 * @return template Merged template
	 * @throws MergeException on processing errors
	 */
	public Template merge() throws MergeException {
		try {
			this.baseTemplate.getMergedOutput();
		} finally {
			if (this.archive != null) {	this.archive.closeOutputStream();}
			for (String provider : this.providers.keySet()) {
				this.providers.get(provider).close();
			}
			
		}
		return this.baseTemplate;
	}
	
	/**
	 * @return archive The merge output archive
	 * @throws MergeException on processing errors
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
			this.archive.setFilePath(config.getTempFolder());
			if (this.mergeData.contians(Merger.IDMU_PARAMETERS + "-" + Merger.IDMU_ARCHIVE_NAME, "-")) {
				this.archive.setFileName(mergeData.get(Merger.IDMU_PARAMETERS + "-" + Merger.IDMU_ARCHIVE_NAME + "-[0]", "-").getAsPrimitive());
			}
		}
		return archive;
	}

	// Simple Getters below here
	
	/**
	 * @return cache The Template Cache
	 */
	public Cache getCahce() {
		return cache;
	}

	/**
	 * Get the cache config object
	 * @return the configuration
	 */
	public Config getConfig() {
		return this.config;
	}
	
	/**
	 * @param templateName The Template to get
	 * @param defaultTemplate The Default template to get if Template not found
	 * @param replace The replace list to initialize
	 * @return template - Get a mergable template from the cache
	 * @throws MergeException on processing errors
	 */
	public Template getMergable(String templateName, String defaultTemplate, HashMap<String,String> replace) throws MergeException {
		return cache.getMergable(this, templateName, defaultTemplate, replace);
	}
	
	/**
	 * @param templateName The Template to get
	 * @return template - A mergable template from the cache
	 * @throws MergeException on processing errors
	 */
	public Template getMergable(String templateName) throws MergeException {
		return cache.getMergable(this, templateName);
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
	 * @param directive The enrich directive
	 * @return provider A Data Provider
	 * @throws MergeException on processing errors
	 */
	public ProviderInterface getProvider(Enrich directive) throws MergeException {
		String className = directive.getEnrichClass();
		String source = directive.getEnrichSource();
		String parameter = directive.getEnrichParameter();
		String key = source.concat(":").concat(parameter);
		if (!this.providers.containsKey(key)) {
			providers.put(key,  
				config.getProviderInstance(className, source, parameter));
		}
		return providers.get(key);
	}

	/**
	 * The templateStack represents the current insert context and the size indicates the level of sub-templates
	 * @return merging template stack
	 */
	public ArrayList<String> getTemplateStack() {
		return templateStack;
	}

	/**
	 * @param name Template Name to push on to insert stack
	 * @param context The merge context
	 */
	public void pushTemplate(String name, DataElement context) {
		templateStack.add(name);
		this.mergeData.pushContext(context);
	}

	/**
	 * remove a template from the stack
	 */
	public void popTemplate() {
		if (templateStack.size() > 0) {
			templateStack.remove(templateStack.size()-1);
			this.mergeData.popContext();
		}
	}

	/**
	 * @return providers
	 */
	public HashMap<String, ProviderInterface> getProviders() {
		return providers;
	}

	/**
	 * @return Template Stack Size (nested sub-template level)
	 */
	public int getStackSize() {
		return templateStack.size();
	}
	
	
	

	// Simple Getters below here
	
	/**
	 * @return template The Base template
	 */
	public Template getBaseTemplate() {
		return baseTemplate;
	}

	// Enumeration Constants
	public static final String IDMU_CONTEXT			= "idmuContext";
	public static final String IDMU_PARAMETERS	 	= "idmuParameters";
	public static final String IDMU_PAYLOAD			= "idmuPayload";
	public static final String IDMU_ARCHIVE			= "idmuArchive";
	public static final String IDMU_ARCHIVE_TYPE	= "idmuArchiveType";
	public static final String IDMU_ARCHIVE_NAME	= "idmuArchiveName";
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
}
