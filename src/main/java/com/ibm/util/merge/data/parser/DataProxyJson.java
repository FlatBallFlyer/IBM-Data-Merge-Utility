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
package com.ibm.util.merge.data.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.gson.DataDeserializer;
import com.ibm.util.merge.data.parser.gson.DataSerializer;
import com.ibm.util.merge.data.parser.gson.DirectiveDeserializer;
import com.ibm.util.merge.data.parser.gson.SourceDeserializer;
import com.ibm.util.merge.template.directive.AbstractDirective;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;

/**
 *
 */
public class DataProxyJson {

    protected GsonBuilder builder;

    public DataProxyJson() {
        builder = new GsonBuilder();
        registerDefaultAdapters();
    }

    public DataProxyJson(GsonBuilder builder) {
        this.builder = builder;
    }

    public <T> T fromJSON(String jsonString, Class<T> type) {
        Gson gson = builder.create();
        return gson.fromJson(jsonString, type);
    }

    public String toJson(Object content) {

        Gson gson = builder.create();
        return gson.toJson(content);
    }

    protected void registerDefaultAdapters() {
        builder.registerTypeAdapter(AbstractDirective.class, new DirectiveDeserializer());
//        builder.registerTypeAdapter(AbstractDirective.class, new DirectiveSerializer());
        builder.registerTypeAdapter(AbstractSource.class, new SourceDeserializer());
//        builder.registerTypeAdapter(AbstractSource.class, new SourceSerializer());
        builder.registerTypeAdapter(DataElement.class, new DataDeserializer());
        builder.registerTypeAdapter(DataElement.class, new DataSerializer());
//        builder.registerTypeAdapter(DataPrimitive.class, new DataPrimitiveCreator());        
    }

}
