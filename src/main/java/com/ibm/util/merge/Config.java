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

import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;

/**
 * The Class Config, represents the IDMU Configuration values.
 * @see Class AbstractSource
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class Config {
	
	public static final int MAX_NEST 	= 20;
	private static final DataProxyJson proxy = new DataProxyJson();
	
	private String tempFolder			= "/opt/ibm/idmu/temp";
	
	public Config() throws Merge500 {
		this.setupDefaults();
	}
	
	public Config(String configString) throws Merge500 {
		this.setupDefaults();
		if (configString.isEmpty()) {
			configString = System.getenv("idmu-config");
		}
		Config me = proxy.fromJSON(configString, Config.class); 
	}
	
	private void setupDefaults() throws Merge500 {
		tempFolder		= "/opt/ibm/idmu/temp";
	}

	public String getTempFolder() {
		return tempFolder;
	}

	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}
}
