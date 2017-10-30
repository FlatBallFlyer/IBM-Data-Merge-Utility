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
/*
 * 
 */
package com.ibm.util.merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.storage.*;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;

/**
 * The Class Merger is the primary interface to IDMU. To use this class
 * Construct a Template Cache, and a Config object, then instantiate a Merger
 * and perform the merge.
 * 
 * Example:
 * 		Config config = new Config();
 * 		TemplateCache cache = new TemplateCache(config);
 * 		Merger merger = new Merger(cache, config, "template.name.");
 * 		merger.merge();
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Merger {
	// Version Info
	public static final String IDMU_VESION			= "4.0.0.0";
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
	
	// Enumeration List 
	public static final HashMap<String, HashSet<String>> ENUMS() {
		HashMap<String, HashSet<String>> myEnums = new HashMap<String, HashSet<String>>(); 
		myEnums.put(DATA_SOURCE, DATA_SOURCES());
		return myEnums;
	}

	private Config config;
	private TemplateCache cahce;
	private Template baseTemplate;
	private DataManager mergeData;
	private ArrayList<String> templateStack;
	HashMap<String,AbstractProvider> providers;
	private Archive archive;
	
	/**
	 * Instantiates a new merge context.
	 *
	 * @param cache the cache
	 * @param sources the sources
	 * @param request the request
	 * @param id the id
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
		this.providers = new HashMap<String, AbstractProvider>();
	}
	
	/**
	 * Instantiates a new merge context.
	 *
	 * @param cache the cache
	 * @param sources the sources
	 * @param request the request
	 * @param id the id
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
	 * @return template - with merge completed
	 * @throws MergeException 
	 */
	public Template merge() throws MergeException {
		this.baseTemplate.getMergedOutput();
		return this.baseTemplate;
	}
	
	public Template getMergable(String templateName, String defaultTemplate, HashMap<String,String> replace) throws MergeException {
		Template template = cahce.getMergable(this, templateName, defaultTemplate, replace);
		this.pushTemplate(template.getId().shorthand());
		return template;
	}
	
	public int getStackSize() {
		return templateStack.size();
	}
	
	public void pushTemplate(String name) {
		templateStack.add(name);
	}
	
	public void popTemplate() {
		if (templateStack.size() > 0) {
			templateStack.remove(templateStack.size()-1);
		}
	}
	
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

	public Config getConfig() {
		return config;
	}

	public DataManager getMergeData() {
		return mergeData;
	}

	public TemplateCache getCahce() {
		return cahce;
	}

	public Template getBaseTemplate() {
		return baseTemplate;
	}

	public HashMap<String, AbstractProvider> getProviders() {
		return providers;
	}

	public ArrayList<String> getTemplateStack() {
		return templateStack;
	}

	public void clearMergeData() {
		this.mergeData.clear();
		
	}
}
