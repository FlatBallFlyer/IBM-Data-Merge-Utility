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
package com.ibm.util.merge.template.directive.enrich.source;

import java.util.HashMap;

public class SourceList {
	private HashMap<String,AbstractSource> sources = new HashMap<String, AbstractSource>();

	public void add(String name, AbstractSource source) {
		sources.put(name, source);
	}
	
	public AbstractSource get(String name) {
		return sources.get(name);
	}
	
	public int size() {
		return sources.size();
	}

	public void remove(String name) {
		sources.remove(name);
	}
	
	public HashMap<String,AbstractSource> getSources() {
		return this.sources;
	}
	
	public void put(String name, AbstractSource source) {
		this.sources.put(name, source);
	}

	public void putAll(HashMap<String, AbstractSource> sources) {
		this.sources.putAll(sources);
	}

	public void putAll(SourceList sources) {
		this.sources.putAll(sources.getSources());
		
	}
	
}
