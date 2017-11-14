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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
 * This is the class that contains IDMU Configuration values and abstracts access
 * to System Environment variables..
 * 
 * @author Mike Storey
 * @since: v4.0.0.B1
 */
public class Configuration {
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
	
	private final String version = "4.0.0.B1";
	private int nestLimit 		= 2;
	private int insertLimit		= 20;
	private String tempFolder	= "/opt/ibm/idmu/archives";
	private String loadFolder	= "foo";
	private String logLevel 	= "SEVERE";
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
	private transient HashMap<String, Class<ProviderInterface>> providers = new HashMap<String, Class<ProviderInterface>>();
	private transient HashMap<Integer, ParserProxyInterface> proxies = new HashMap<Integer, ParserProxyInterface>();
	
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
		Configuration me = proxy.fromString(configString, Configuration.class);
		this.nestLimit = me.getNestLimit();
		this.insertLimit = me.insertLimit;
		this.tempFolder = me.getTempFolder();
		this.loadFolder = me.getLoadFolder();
		this.logLevel = me.getLogLevel();
		this.envVars = me.getEnvVars();
		this.proxies = new HashMap<Integer, ParserProxyInterface>();
		if (null != me.defaultParsers) {
			this.defaultParsers = me.defaultParsers;
		}
		this.registerDefaultProxies();
		
		this.providers = new HashMap<String, Class<ProviderInterface>>();
		if (null != me.defaultProviders) {
			this.defaultProviders = me.defaultProviders;
		}
		this.registerDefaultProviders();
		
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
	
	public DataElement parse(int parseAs, String value) throws MergeException {
		Integer key = new Integer(parseAs);
		if (parseAs == Configuration.PARSE_NONE) {
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
	
	public HashMap<String, Class<ProviderInterface>> getProviders() {
		return this.providers;
	}
	
	public String getAllOptions() throws MergeException {
		HashMap<String, HashMap<String, HashMap<Integer, String>>> selectValues;
		HashMap<String, ProviderMeta> providerList;

		selectValues = new HashMap<String, HashMap<String, HashMap<Integer, String>>>();
		providerList = new HashMap<String, ProviderMeta>();
		
		selectValues.put("Template", 	Template.getOptions());
		selectValues.put("Encoding",  Segment.getOptions());
//		selectValues.put("Parsers", 	Configuration.getOptions());
//		selectValues.put("Providers", 	this.providers.keySet());
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
