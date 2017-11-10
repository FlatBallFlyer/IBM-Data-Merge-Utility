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

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * Implements a Singleton Pattern for a Configuration object
 * 
 * @author Mike Storey
 * @since: v4.0.0.B1
 */
public class Config {
	private static Configuration config = null;
	
	public static Configuration initialize() throws Merge500 {
		config = new Configuration();
		return config;
	}

	public static Configuration get() throws Merge500 {
		if (null == config) {
			config = new Configuration();
		}
		return config;
	}

	public static Configuration load(String name) throws MergeException {
		return Config.get().initialize(name);
	}
	
}
