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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.data.parser.Parsers;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Segment;
import com.ibm.util.merge.template.directive.Enrich;
import com.ibm.util.merge.template.directive.Insert;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.Replace;
import com.ibm.util.merge.template.directive.SaveFile;
import com.ibm.util.merge.template.directive.enrich.provider.ProviderInterface;
import com.ibm.util.merge.template.directive.enrich.provider.ProviderMeta;
import com.ibm.util.merge.template.directive.enrich.provider.Providers;

/**
 * Provides configuration information for the Merge Utility -  
 * Implemented as a singleton with static convenience methods
 *
 * <p>All configuration constructor values accept a JSON string. 
 * This string only needs to provide values when you want to override a default. </p>
 * <p>
 * Both the Default Providers and Parsers lists must be complete, providing any 
 * value removes all default entries. If you add a custom provider, you will need to 
 * list all of the default providers you want as well as your new provider.</p>
 * <p>Configuration Format JSON<blockquote><pre>
 * {
 * 	"nestLimit": n,
 * 	"insertLimit": n,
 * 	"tempFolder": "folder",
 *  "loadFolder" : "folder",
 *  "prettyJson" : true | false
 * 	"logLevel": "CRITICAL | SEVERE | WARN | INFO",
 * 	"envVars" : {"var":"value"},
 *	"defaultProviders" : ["providerClass","providerClass"],
 *	"defaultParsers" : ["parserClass","parserClass"],		
 * }
 * </pre></blockquote>
 * 
 * @author Mike Storey
 * @since: v4.0.0.B1
 */
public class Config {
	private final String version = "4.0.0.B3";
	private int nestLimit 		= 2;
	private int insertLimit		= 20;
	private String tempFolder	= "/opt/ibm/idmu/v4/archives";
	private String loadFolder	= "/opt/ibm/idmu/v4/packages";
	private String logLevel 	= "SEVERE";
	private boolean prettyJson = true;
	private HashMap<String, String> envVars = new HashMap<String,String>();
	private String[] defaultProviders = {
			"com.ibm.util.merge.template.directive.enrich.provider.CacheProvider",
			"com.ibm.util.merge.template.directive.enrich.provider.CloudantProvider",
			"com.ibm.util.merge.template.directive.enrich.provider.FileSystemProvider",
			"com.ibm.util.merge.template.directive.enrich.provider.JdbcProvider",
			"com.ibm.util.merge.template.directive.enrich.provider.JndiProvider",
			"com.ibm.util.merge.template.directive.enrich.provider.MongoProvider",
			"com.ibm.util.merge.template.directive.enrich.provider.RestProvider",
			"com.ibm.util.merge.template.directive.enrich.provider.StubProvider"
	};
	private String[] defaultParsers = {
			"com.ibm.util.merge.data.parser.DataProxyCsv",
			"com.ibm.util.merge.data.parser.DataProxyJson",
			"com.ibm.util.merge.data.parser.DataProxyXmlStrict"
	};

	/*
	 * Transient Values - Providers, Parsers and a JSON Proxy
	 */
	private transient Providers providers = new Providers();
	private transient Parsers proxies = new Parsers();
	private transient static DataProxyJson proxy;
	
	/**
	 * Provide a default configuration. If the enviornment variable
	 * idmu-config exists it is parsed as a json configuration value. 
	 * If the config environment variable does not exist, all defaults
	 * are provided.
	 * 
	 * @throws MergeException on Processing Errors
	 */
	public Config() throws MergeException {
		String configString = "";
		try {
			configString = this.getEnv("idmu-config");
		} catch (Throwable e) {
			// ignore
		}
	    Logger rootLogger = LogManager.getLogManager().getLogger("");
	    rootLogger.setLevel(Level.parse(this.logLevel));
		loadConfig(configString);
	}
	
	/**
	 * Get a configuration from the provided environment variable name. 
	 * 
	 * @param configString The configuration JSON
	 * @throws MergeException on Processing Errors
	 */
	public Config(String configString) throws MergeException {
	    Logger rootLogger = LogManager.getLogManager().getLogger("");
	    rootLogger.setLevel(Level.parse(this.logLevel));
		loadConfig(configString);
	}
	
	/**
	 * Read a configuration from a config file
	 * 
	 * @param configFile The configuration file
	 * @throws MergeException on Processing Errors
	 */
	public Config(File configFile) throws MergeException {
		String configString;
		try {
			configString = new String(Files.readAllBytes(configFile.toPath()), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new Merge500("Unsupported Encoding Exception reading config file: " + configFile.toString() + " Message: "+ e.getMessage());
		} catch (IOException e) {
			throw new Merge500("IO Exception reading config file: " + configFile.toString() + " Message: " + e.getMessage());
		} 
	    Logger rootLogger = LogManager.getLogManager().getLogger("");
	    rootLogger.setLevel(Level.parse(this.logLevel));
		loadConfig(configString);
	}
	
	/**
	 * Read a configuration from an anonymous http source
	 * 
	 * @param url The URL to fetch a config from
	 * @throws MergeException on Processing Errors
	 */
	public Config(URL url) throws MergeException {
		String configString;
		try {
			configString = IOUtils.toString(
					new BufferedReader(
					new InputStreamReader(
					url.openStream())));
		} catch (IOException e) {
			throw new Merge500("Unable to laod config " + url.toString());
		}
	    Logger rootLogger = LogManager.getLogManager().getLogger("");
	    rootLogger.setLevel(Level.parse(this.logLevel));
		loadConfig(configString);
	}
	
	/**
	 * Load the configuration provided
	 * 
	 * @param configString The configuration JSON
	 * @throws MergeException on Processing Errors
	 */
	private void loadConfig(String configString) throws MergeException {
	    proxy = new DataProxyJson(false);
		if (null != configString) {
			JsonElement ele = proxy.fromString(configString, JsonElement.class);
			if (null != ele && ele.isJsonObject()) {
				JsonObject me = ele.getAsJsonObject();
				this.nestLimit 		= this.getIf(me, "nestLimit", this.nestLimit);
				this.insertLimit 	= this.getIf(me, "insertLimit", this.insertLimit);
				this.tempFolder 		= this.getIf(me, "tempFolder", this.tempFolder);
				this.loadFolder 		= this.getIf(me, "loadFolder", this.loadFolder);
				this.prettyJson		= this.getIf(me, "prettyJson", true);
				this.logLevel 		= this.getIf(me, "logLevel", this.logLevel);
				if (me.has("envVars") && me.get("envVars").isJsonObject()) {
					this.envVars = new HashMap<String,String>();
					for (Entry<String, JsonElement> var : me.get("envVars").getAsJsonObject().entrySet()) {
						this.envVars.put(var.getKey(), var.getValue().getAsString());
					}
				}
				
				if (me.has("defaultProviders") && me.get("defaultProviders").isJsonArray()) {
					JsonArray list = me.get("defaultProviders").getAsJsonArray();
					this.defaultProviders = new String[list.size()];
					for (int i = 0; i < list.size(); i++ ) {
						this.defaultProviders[i] = list.get(i).getAsString();
					}
				}
				
				if (me.has("defaultParsers")) {
					JsonArray list = me.get("defaultParsers").getAsJsonArray();
					this.defaultParsers = new String[list.size()];
					for (int i = 0; i < list.size(); i++ ) {
						this.defaultParsers[i] = list.get(i).getAsString();
					}
				}
			}
		}
		
		this.registerDefaultProxies();
		this.registerDefaultProviders();
		
	    Logger rootLogger = LogManager.getLogManager().getLogger("");
	    rootLogger.setLevel(Level.parse(this.logLevel));
	    proxy = new DataProxyJson(this.isPrettyJson());
	}
	
	private int getIf(JsonObject me, String name, int value) {
		if (me.has(name)) {
			return me.get(name).getAsInt();
		} else {
			return value;
		}
	}

	private String getIf(JsonObject object, String name, String value) {
		if (object.has(name)) {
			return object.get(name).getAsString();
		} else {
			return value;
		}
	}

	private Boolean getIf(JsonObject object, String name, Boolean value) {
		if (object.has(name)) {
			return object.get(name).getAsBoolean();
		} else {
			return value;
		}
	}
	
	/**
	 * Abstraction of Environment access. Will leverage an entry from the 
	 * local Environment hashmap property. Environment Variables prefixed with 
	 * "VCAP:" will be treated as entries in the VCAP_SERVICES environment variable.
	 * 
	 * You can provide environment values by adding entries to the envVars hashMap
	 *  
	 * @param name The environment variable
	 * @return The environment value
	 * @throws MergeException on Processing Errors
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
	 * @param serviceName The VCAP Service Name to lookup
	 * @return the VCAP entry
	 * @throws MergeException on Processing Errors
	 */
	public String getVcapEntry(String serviceName) throws MergeException {
		String VCAP_SERVICES = this.getEnv("VCAP_SERVICES");
		String value = "";
		if (null == VCAP_SERVICES) {
			throw new Merge500("VCAP_SERVICES enviornment variable missing");
		}
		
		try {
			DataElement vcap = proxy.fromString(VCAP_SERVICES, DataElement.class);
			value = proxy.toString(vcap.getAsObject().get(serviceName).getAsList().get(0).getAsPrimitive());
		} catch (Exception e) {
			throw new Merge500("VCAP_SERVICES contains malformed JSON or is missing service " + serviceName);
		}
		return value;
	}
	
	//	// Simple Getters below here
	//	
		public Boolean isPrettyJson() {
			return prettyJson;
		}

	public String getTempFolder() {
		return tempFolder;
	}

	public String getLoadFolder() {
		return loadFolder;
	}

	public int getNestLimit() {
		return nestLimit;
	}

	public int getInsertLimit() {
		return insertLimit;
	}

	public String getVersion() {
		return version;
	}

	// Parser Management
	public void registerDefaultProxies() throws MergeException {
		this.proxies.registerDefaultProxies(this.defaultParsers);
	}
	
	public DataElement parseString(int parseAs, String value, String options, Template context) throws MergeException {
		return this.proxies.parseString(parseAs, value, options, context);
	}
	
	// Provider Management
	public ProviderInterface getProviderInstance(String className, String source, String parameter) throws MergeException {
		return this.providers.getProviderInstance(className, source, parameter);
	}
	
	public void registerDefaultProviders() throws MergeException {
		this.providers.registerDefaultProviders(this.defaultProviders);
	}

	/*
	 * Constants and Options
	 */
	public static final int PARSE_NONE	= 4;
	public static final int PARSE_CSV	= 1;
	public static final int PARSE_JSON	= 3;
	public static final int PARSE_XML	= 5;
	public static final HashMap<Integer, String> PARSE_OPTIONS() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(PARSE_CSV, 	"csv");
		values.put(PARSE_JSON, 	"json");
		values.put(PARSE_NONE, 	"none");
		values.put(PARSE_XML,	"xml");
		return values;
	}

	public static final HashMap<String,HashMap<Integer, String>> getOptions() {
		HashMap<String,HashMap<Integer, String>> options = new HashMap<String,HashMap<Integer, String>>();
		options.put("Parse Formats", PARSE_OPTIONS());
		return options;
	}
	
	/**
	 * Build a configuration object and return the json
	 * @return A json string of all configuration and template/directive options
	 * @throws MergeException on build errors.
	 */
	public String get() throws MergeException {
		// Build the return object and Config values
		DataObject returnObject = new DataObject();
		DataObject config = new DataObject();
		config.put("version", new DataPrimitive(version));
		config.put("nestLimit", new DataPrimitive(nestLimit));
		config.put("insertLimit", new DataPrimitive(insertLimit));
		config.put("tempFolder", new DataPrimitive(tempFolder));
		config.put("logLevel", new DataPrimitive(logLevel));
		config.put("prettyJson", new DataPrimitive(prettyJson));
		DataObject env = new DataObject();
		for (String key : envVars.keySet()) {
			env.put(key, new DataPrimitive(envVars.get(key)));
		}
		returnObject.put("config", config);
		
		// Build the Providers List
		DataObject providers = new DataObject();
		for (String provider : this.providers.keySet()) {
			ProviderMeta meta = this.getProviderInstance(provider, "source", "parameters").getMetaInfo();
			DataObject providerData = new DataObject();
			providerData.put("optionName", new DataPrimitive(meta.optionName));
			providerData.put("sourceJson", new DataPrimitive(meta.sourceEnv));
			providerData.put("commandHelp", new DataPrimitive(meta.commandHelp));
			providerData.put("parseHelp", new DataPrimitive(meta.parseHelp));
			providerData.put("returnHelp", new DataPrimitive(meta.returnHelp));
			providers.put(provider, providerData);
		}
		returnObject.put("providers", providers);
		
		// Build Parser List
		DataObject parsers = new DataObject();
		for (Integer parser : this.proxies.keySet()) {
			parsers.put(Integer.toString(parser), new DataPrimitive(proxies.get(parser).getClass().getName()));
		}
		returnObject.put("parsers", parsers);
		
		// Build the Object Enum Options list
		returnObject.put("template",	theOptions(Template.getOptions()));
		returnObject.put("encoding",	theOptions(Segment.getOptions()));
		returnObject.put("enrich", 	theOptions(Enrich.getOptions()));
		returnObject.put("insert", 	theOptions(Insert.getOptions()));
		returnObject.put("parse", 	theOptions(ParseData.getOptions()));
		returnObject.put("replace", 	theOptions(Replace.getOptions()));
		returnObject.put("save", 	theOptions(SaveFile.getOptions()));
		
		return proxy.toString(returnObject);
	}
	
	/**
	 * Helper for "get" method
	 * @param values Options from an Object getOptions
	 * @return the Options converted to a DataElement
	 */
	public DataElement theOptions(HashMap<String, HashMap<Integer, String>> values) {
		DataObject enums = new DataObject();
		for (String name : values.keySet()) {
			DataObject options = new DataObject();
			enums.put(name, options);
			for (Integer option : values.get(name).keySet()) {
				options.put(Integer.toString(option), new DataPrimitive(values.get(name).get(option)));
			}
		}
		return enums;
	}

	/**
	 * Check if the parse as parser supported
	 * @param parseAs the parse format
	 * @return true if the parse format is supported
	 */
	public boolean hasParser(int parseAs) {
		return (this.proxies.containsKey(new Integer(parseAs)));
	}

	/**
	 * Check if the provider is supported
	 * @param key provider class
	 * @return true if it's a supported provider
	 */
	public boolean hasProvider(String key) {
		return (this.providers.containsKey(key));
	}
}

