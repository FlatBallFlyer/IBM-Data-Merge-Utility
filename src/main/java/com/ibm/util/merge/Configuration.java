/*
 * 
 * Copyright 2015-2017 IBM
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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Segment;
import com.ibm.util.merge.template.directive.Enrich;
import com.ibm.util.merge.template.directive.Insert;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.Replace;
import com.ibm.util.merge.template.directive.SaveFile;

/**
 * This is the class that contains IDMU Configuration values and abstracts access
 * to System Environment variables..
 * 
 * @author Mike Storey
 * @since: v4.0.0.B1
 */
public class Configuration {
	private int nestLimit 		= 2;
	private int insertLimit		= 20;
	private String tempFolder	= "/opt/ibm/idmu/archives";
	private String loadFolder	= "foo";
	private String logLevel 	= "SEVERE";
	private final String version = "4.0.0.B1";
	private HashMap<String, String> envVars = new HashMap<String,String>();

	private transient static final DataProxyJson proxy = new DataProxyJson();
	
	/**
	 * Provide a default configuration
	 * 
	 * @throws Merge500
	 */
	public Configuration() throws Merge500 {
	    Logger rootLogger = LogManager.getLogManager().getLogger("");
	    rootLogger.setLevel(Level.parse(this.logLevel));
	}
	
	/**
	 * Get a configuration from the provided environment variable name. 
	 * If an empty string is provided the default "idmu-config" environment
	 * variable is used. 
	 * 
	 * @param configString
	 * @throws MergeException
	 */
	public Configuration initialize(String configString) throws MergeException {
		if (configString.isEmpty()) {
			configString = this.getEnv("idmu-config");
		}
		Configuration me = proxy.fromJSON(configString, Configuration.class);
		this.nestLimit = me.getNestLimit();
		this.insertLimit = me.insertLimit;
		this.tempFolder = me.getTempFolder();
		this.loadFolder = me.getLoadFolder();
		this.logLevel = me.getLogLevel();
		this.envVars = me.getEnvVars();
	    Logger rootLogger = LogManager.getLogManager().getLogger("");
	    rootLogger.setLevel(Level.parse(this.logLevel));
	    return this;
	}
	
	/**
	 * Abstraction of Environment access. Will leverage an entry from the 
	 * local Environment hashmap property. Environment Variables prefixed with 
	 * "VCAP:" will be treated as entries in the VCAP_SERVICES environment variable.
	 * 
	 * You can provide environment values by adding entries to the envVars hashMap
	 *  
	 * @param name
	 * @return
	 * @throws MergeException
	 */
	public String getEnv(String name) throws MergeException {
		if (envVars.containsKey(name)) {
			return envVars.get(name);
		}
		
		if (name.startsWith("VCAP:")) {
			return getVcapEntry(name.substring(5));
		}
		
		String value = System.getenv(name);
		if (null == value) {
			throw new Merge500("enviornment variable not found");
		}
		return value;
	}
	
	/**
	 * @param serviceName
	 * @return
	 * @throws MergeException
	 */
	private String getVcapEntry(String serviceName) throws MergeException {
		String VCAP_SERVICES = this.getEnv("VCAP_SERVICES");
		String value = "";
		if (null == VCAP_SERVICES) {
			throw new Merge500("VCAP_SERVICES enviornment variable missing");
		}
		
		try {
			DataElement vcap = proxy.fromJSON(VCAP_SERVICES, DataElement.class);
			value = proxy.toJson(vcap.getAsObject().get(serviceName).getAsList().get(0).getAsPrimitive());
		} catch (Exception e) {
			throw new Merge500("VCAP_SERVICES contains malformed JSON or is missing service " + serviceName);
		}
		return value;
	}
	
	// Simple Getter/Setter below here
	
	/**
	 * File Folder where archives are created. 
	 * 
	 * @return
	 */
	public String getTempFolder() {
		return tempFolder;
	}

	/**
	 * File Folder where archives are created. 
	 * 
	 * @param tempFolder
	 */
	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}
	
	/**
	 * Limit for nested Replace tags
	 *  
	 * @return nesting limit
	 */
	public int getNestLimit() {
		return nestLimit;
	}
	
	/**
	 * Limit for nested Replace tags
	 *  
	 * @param limit
	 */
	public void setNestLimit(int limit) {
		this.nestLimit = limit;
	}
	
	/**
	 * Limit of Sub-Template Insert depth - recursion safety catch
	 * 
	 * @return insert limit
	 */
	public int getInsertLimit() {
		return insertLimit;
	}

	/**
	 * Limit of Sub-Template Insert depth - recursion safety catch
	 * 
	 * @param insertLimit
	 */
	public void setInsertLimit(int insertLimit) {
		this.insertLimit = insertLimit;
	}

	/**
	 * HashMap of default environment values - helpful for testing
	 * 
	 * @return
	 */
	public HashMap<String, String> getEnvVars() {
		return envVars;
	}

	/**
	 * HashMap of default environment values - helpful for testing
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	public String getLoadFolder() {
		return loadFolder;
	}

	public void setLoadFolder(String loadFolder) {
		this.loadFolder = loadFolder;
	}
	
	public String getLogLevel() {
		return this.logLevel;
	}
	
	public void setLogLevel(String level) {
		this.logLevel = level;
	}

	public String getAllOptions() {
		HashMap<String, HashMap<String, HashMap<Integer, String>>> values = 
				new HashMap<String, HashMap<String, HashMap<Integer, String>>>();
		values.put("Template", 	Template.getOptions());
		values.put("Encoding",  Segment.getOptions());
		values.put("Parser", 	Parser.getOptions());
		values.put("Enrich", 	Enrich.getOptions());
		values.put("Insert", 	Insert.getOptions());
		values.put("Parse", 	ParseData.getOptions());
		values.put("Replace", 	Replace.getOptions());
		values.put("Save", 		SaveFile.getOptions());
		return proxy.toJson(values);
	}
	
}
