/*
 * 
 * Copyright 2015, 2015 IBM
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

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * The Class Config, represents the IDMU Configuration values.
 * @see Class AbstractSource
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Config {
	private int nestLimit 		= 2;
	private int insertLimit		= 20;
	private String tempFolder	= "/opt/ibm/idmu/temp";
	private HashMap<String, String> envVars;

	private static final DataProxyJson proxy = new DataProxyJson();
	
	public Config() throws Merge500 {
		this.setupDefaults();
	}
	
	public Config(String configString) throws MergeException {
		this.setupDefaults();
		if (configString.isEmpty()) {
			configString = this.getEnv("idmu-config");
		}
		Config me = proxy.fromJSON(configString, Config.class);
		this.nestLimit = me.getNestLimit();
		this.insertLimit = me.insertLimit;
		this.tempFolder = me.getTempFolder();
		this.envVars = me.getEnvVars();
	}
	
	private void setupDefaults() throws Merge500 {
		tempFolder	= "/opt/ibm/idmu/temp";
		nestLimit 	= 2;
		insertLimit = 20;
		envVars 	= new HashMap<String,String>();
	}

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
	
	public String getVcapEntry(String serviceName) throws MergeException {
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
}
