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
package com.ibm.util.merge.data.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.gson.DataDeserializer;
import com.ibm.util.merge.data.parser.gson.DataSerializer;
import com.ibm.util.merge.data.parser.gson.DirectiveDeserializer;
import com.ibm.util.merge.data.parser.gson.DirectiveSerializer;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.AbstractDirective;

/**
 * Provies for parsing of JSON data
 * @author Mike Storey
 *
 */
public class DataProxyJson implements ParserProxyInterface {

    protected GsonBuilder builder;

    /**
     * Instantiate a Proxy
     * @throws MergeException 
     */
    public DataProxyJson() {
        builder = new GsonBuilder();
        registerDefaultAdapters();
    }

    /**
     * Instantiate a Proxy
     * @throws MergeException 
     */
    public DataProxyJson(Boolean pretty) {
        builder = new GsonBuilder();
        registerDefaultAdapters();
        if (pretty) {builder.setPrettyPrinting();}
    }

    /**
     * Instantiate a proxy with a BUilder
     * @param builder GsonBuilder to use in parsing
     */
    public DataProxyJson(GsonBuilder builder) {
        this.builder = builder;
    }

	@Override
	public Integer getKey() {
		return new Integer(3);
	}

    /**
     * Parse the JSON Data into an object
     * 
     * @param jsonString The string to parse
     * @param type The class to parse into
     * @param <T> The class Type to parse into
     * @return The instantiated object
     */
    public <T> T fromString(String jsonString, Class<T> type) {
        Gson gson = builder.create();
        return gson.fromJson(jsonString, type);
    }

    public DataElement fromString(String value) {
        Gson gson = builder.create();
    	return gson.fromJson(value, DataElement.class);
    }
    
    public String toString(Object src) {
        Gson gson = builder.create();
        return gson.toJson(src);
    }

    /**
     * Register custom adapters for Template Directives and DataElement objects
     */
    protected void registerDefaultAdapters() {
        builder.registerTypeAdapter(AbstractDirective.class, new DirectiveDeserializer());
        builder.registerTypeAdapter(AbstractDirective.class, new DirectiveSerializer());
        builder.registerTypeAdapter(DataElement.class, new DataDeserializer());
        builder.registerTypeAdapter(DataElement.class, new DataSerializer());
//        builder.registerTypeAdapter(DataPrimitive.class, new DataPrimitiveCreator());        
    }

}
