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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.data.parser.ParserProxyInterface;
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

/**
 * Provides configuration information for the Merge Utility
 * You can optionally load a configuration with one of 
 * the provided load functions.
 *
 * Implemented as a singleton with static convenience methods
 * 
 * @author Mike Storey
 * @since: v4.0.0.B1
 */
public class Config {
	/* ******************************************************************************************************
	 * Singleton Code 
	 */
	private static Config config = null;
	
	/**
	 * Singleton Constructor
	 * @return the singleton config
	 * @throws MergeException on Processing Errors
	 */
	private static Config get() throws MergeException {
		if (null == config) {
			config = new Config();
		}
		return config;
	}

	/**
	 * @throws MergeException on Processing Errors
	 */
	public static void initialize() throws MergeException {
		config = new Config();
	}
	
	/**
	 * @param configuration The JSON Config object to load
	 * @throws MergeException on Processing Errors
	 */
	public static void load(String configuration) throws MergeException {
		config = new Config(configuration);
	}
	
	/**
	 * @param configFile The JSON Configuration file
	 * @throws MergeException on Processing Errors
	 */
	public static void load(File configFile) throws MergeException {
		config = new Config(configFile);
	}
	
	/**
	 * @param configUrl The URL to fetch a configuration from
	 * @throws MergeException on Processing Errors
	 */
	public static void load(URL configUrl) throws MergeException {
		config = new Config(configUrl);
	}
	
	/**
	 * @return The nesting limit for Replace Tags
	 * @throws MergeException on Processing Errors
	 */
	public static int nestLimit() throws MergeException {
		return Config.get().getNestLimit();
	}

	/**
	 * @return The insert limit for template depth
	 * @throws MergeException on Processing Errors
	 */
	public static int insertLimit() throws MergeException {
		return Config.get().getInsertLimit();
	}
	
	/**
	 * @return The temporary folder where archives are created
	 * @throws MergeException on Processing Errors
	 */
	public static String tempFolder() throws MergeException {
		return Config.get().getTempFolder();
	}

	/**
	 * @return The template load folder where templates are located
	 * @throws MergeException on Processing Errors
	 */
	public static String loadFolder() throws MergeException {
		return Config.get().getLoadFolder();
	}

	/**
	 * @param varName The variable to get
	 * @return The environment value
	 * @throws MergeException on Processing Errors
	 */
	public static String env(String varName) throws MergeException {
		return Config.get().getEnv(varName);
	}

	/**
	 * @return The IDMU Version
	 * @throws MergeException on Processing Errors
	 */
	public static String version() throws MergeException {
		return Config.get().getVersion();
	}

	/**
	 * @param className The provider class name
	 * @param source The source name
	 * @param option The source option
	 * @param context The merge Context
	 * @return A enrich provider instance
	 * @throws MergeException on Processing Errors
	 */
	public static ProviderInterface providerInstance(String className, String source, String option, Merger context) throws MergeException {
		return Config.get().getProviderInstance(className, source, option, context);
	}
	
	/**
	 * @param parseAs the parse format
	 * @param value the value to parse
	 * @return the parsed data element
	 * @throws MergeException on Processing Errors
	 */
	public static DataElement parse(int parseAs, String value) throws MergeException {
		return Config.get().parseString(parseAs, value);
	}
	
	/**
	 * @return The options json
	 * @throws MergeException on Processing Errors
	 */
	public static String allOptions() throws MergeException {
		return Config.get().getAllOptions();
	}

	/**
	 * @return The Providers hashmap
	 * @throws MergeException on Processing Errors
	 */
	public static HashMap<String, Class<ProviderInterface>> providers() throws MergeException {
		return Config.get().getProviders();
	}
	

	/* *********************************************************
	 * End of Static Convenience accessor's, Normal Non-Static Object Attributes and Methods
	 */
	/**
	 * The IDMU Version
	 */
	private final String version = "4.0.0.B1";
	/**
	 * The limit of nesting on Replace Tags
	 */
	private int nestLimit 		= 2;
	/**
	 * The limit on the number of nested sub-template inserts
	 */
	private int insertLimit		= 20;
	/**
	 * The folder where archives are created
	 */
	private String tempFolder	= "/opt/ibm/idmu/archives";
	/**
	 * The template load folder
	 */
	private String loadFolder	= "foo";
	/**
	 * The logging level
	 */
	private String logLevel 	= "SEVERE";
	/**
	 * Environment Variables used to over-ride values in the system environment
	 */
	private HashMap<String, String> envVars = new HashMap<String,String>();
	/**
	 * The list of Enrichment Providers that can be used
	 */
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
	/**
	 * The list of Parsers that can be used
	 */
	private String[] defaultParsers = {
			"com.ibm.util.merge.data.parser.DataProxyCsv",
			"com.ibm.util.merge.data.parser.DataProxyJson",
			"com.ibm.util.merge.data.parser.DataProxyXmlStrict"
	};

	/*
	 * Transient Values - Providers, Parsers and a JSON Proxy
	 */
	private transient HashMap<String, Class<ProviderInterface>> providers = new HashMap<String, Class<ProviderInterface>>();
	private transient HashMap<Integer, ParserProxyInterface> proxies = new HashMap<Integer, ParserProxyInterface>();
	private transient static final DataProxyJson proxy = new DataProxyJson();
	
	/**
	 * Provide a default configuration. If the enviornment variable
	 * idmu-config exists it is parsed as a json configuration value. 
	 * If the config environment variable does not exist, all defaults
	 * are provided.
	 * 
	 * @throws MergeException on Processing Errors
	 */
	public Config() throws MergeException {
		this.proxies = new HashMap<Integer, ParserProxyInterface>();
		this.providers = new HashMap<String, Class<ProviderInterface>>();
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
		this.proxies = new HashMap<Integer, ParserProxyInterface>();
		this.providers = new HashMap<String, Class<ProviderInterface>>();
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
		this.proxies = new HashMap<Integer, ParserProxyInterface>();
		this.providers = new HashMap<String, Class<ProviderInterface>>();
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
		this.proxies = new HashMap<Integer, ParserProxyInterface>();
		this.providers = new HashMap<String, Class<ProviderInterface>>();
		String configString = ""; // TODO HTTP Get of ConfigString
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
	public void loadConfig(String configString) throws MergeException {
		if (null != configString) {
			JsonElement ele = proxy.fromString(configString, JsonElement.class);
			if (null != ele && ele.isJsonObject()) {
				JsonObject me = ele.getAsJsonObject();
				this.nestLimit 		= this.getIf(me, "nestLimit", this.nestLimit);
				this.insertLimit 	= this.getIf(me, "insertLimit", this.insertLimit);
				this.tempFolder 	= this.getIf(me, "tempFolder", this.tempFolder);
				this.loadFolder 	= this.getIf(me, "loadFolder", this.loadFolder);
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
	 * @param serviceName
	 * @return
	 * @throws MergeException on Processing Errors
	 */
	private String getVcapEntry(String serviceName) throws MergeException {
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
	
	// Parser Management
	public ParserProxyInterface getProxyInstance(int parseAs) throws MergeException {
		Integer proxy = new Integer(parseAs);
		if (!this.proxies.containsKey(proxy)) {
			throw new Merge500("Provider not found, did you register it?");
		}
		return this.proxies.get(proxy);
	}

	public void registerDefaultProxies() throws MergeException {
		proxies = new HashMap<Integer, ParserProxyInterface>();
		for (String proxy : this.defaultParsers) {
			registerProxy(proxy);
		}
	}

	public void registerProxies(String[] proxies) throws MergeException {
		for (String proxy : proxies) {
			registerProxy(proxy);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void registerProxy(String className) throws MergeException {
		Class<ParserProxyInterface> clazz;
		ParserProxyInterface theProxy;
		try {
			clazz = (Class<ParserProxyInterface>) Class.forName(className);
			theProxy = (ParserProxyInterface) clazz.newInstance();
			this.proxies.put(theProxy.getKey(), theProxy);
		} catch (ClassNotFoundException e) {
			throw new Merge500("Class Not Found exception: " + className + " message: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new Merge500("InstantiationException " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Merge500("IllegalAccessException " + e.getMessage());
		}
	}
	
	public DataElement parseString(int parseAs, String value) throws MergeException {
		Integer key = new Integer(parseAs);
		if (parseAs == Config.PARSE_NONE) {
			throw new Merge500("Parse Type is None!");
		}
		if (!this.proxies.containsKey(key)) {
			throw new Merge500("Parser not found, did you register it?" + Integer.toString(parseAs));
		}
		if (null == value) {
			throw new Merge500("Can't Parse Null!");
		}
		ParserProxyInterface proxy = this.proxies.get(parseAs);
		return proxy.fromString(value);
	}
	
	// Provider Management
	public ProviderInterface getProviderInstance(String className, String source, String option, Merger context) throws MergeException {
		if (!this.providers.containsKey(className)) {
			throw new Merge500("Provider not found, did you register it?");
		}
		
		ProviderInterface theProvider;
		try {
			@SuppressWarnings("rawtypes")
			Class[] cArg = new Class[3]; 
			cArg[0] = String.class;
			cArg[1] = String.class;
			cArg[2] = Merger.class;
			theProvider = (ProviderInterface) this.providers.get(className).getDeclaredConstructor(cArg).newInstance(source, option, context);
		} catch (InstantiationException e) {
			throw new Merge500("Error instantiating class: " + className + " message: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Merge500("Error accessing class: " + className + " message: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new Merge500("IllegalArgumentException : " + className + " message: " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new Merge500("InvocationTargetException: " + className + " message: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new Merge500("NoSuchMethodException: " + className + " message: " + e.getMessage());
		} catch (SecurityException e) {
			throw new Merge500("Error accessing class: " + className + " message: " + e.getMessage());
		}
		
		return theProvider;
	}
	
	public void registerDefaultProviders() throws MergeException {
		providers = new HashMap<String, Class<ProviderInterface>>();
		for (String provider : this.defaultProviders) {
			registerProvider(provider);
		}
	}

	public void registerProviders(String[] providers) throws MergeException {
		for (String provider : providers) {
			registerProvider(provider);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void registerProvider(String className) throws MergeException {
		Class<ProviderInterface> clazz;
		try {
			clazz = (Class<ProviderInterface>) Class.forName(className);
			this.providers.put(className, clazz);
		} catch (ClassNotFoundException e) {
			throw new Merge500("Class Not Found exception: " + className + " message: " + e.getMessage());
		}
	}
	
	// Simple Getter/Setter below here
	
	public String getTempFolder() {
		return tempFolder;
	}

	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}
	
	public int getNestLimit() {
		return nestLimit;
	}
	
	public void setNestLimit(int limit) {
		this.nestLimit = limit;
	}
	
	public int getInsertLimit() {
		return insertLimit;
	}

	public void setInsertLimit(int insertLimit) {
		this.insertLimit = insertLimit;
	}

	public HashMap<String, String> getEnvVars() {
		return envVars;
	}

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
	
	public HashMap<String, Class<ProviderInterface>> getProviders() {
		return this.providers;
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
	
	public String getAllOptions() throws MergeException {
		HashMap<String, HashMap<String, HashMap<Integer, String>>> selectValues;
		HashMap<String, ProviderMeta> providerList;

		selectValues = new HashMap<String, HashMap<String, HashMap<Integer, String>>>();
		providerList = new HashMap<String, ProviderMeta>();
		
		selectValues.put("Template", 	Template.getOptions());
		selectValues.put("Encoding",  Segment.getOptions());
		selectValues.put("Enrich", 	Enrich.getOptions());
		selectValues.put("Insert", 	Insert.getOptions());
		selectValues.put("Parse", 	ParseData.getOptions());
		selectValues.put("Replace", 	Replace.getOptions());
		selectValues.put("Save", 		SaveFile.getOptions());
		
		for (String provider : this.providers.keySet()) {
			providerList.put(provider, this.getProviderInstance(provider, "", "", null).getMetaInfo());
		}
		String value = "[".concat(proxy.toString(selectValues)).concat(",").concat(proxy.toString(providerList)).concat("]");
		return value;
	}

}
