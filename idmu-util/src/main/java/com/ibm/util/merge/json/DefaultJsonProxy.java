package com.ibm.util.merge.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.util.merge.directive.Directive;
import com.ibm.util.merge.directive.DirectiveDeserializer;
import com.ibm.util.merge.directive.provider.Provider;
import com.ibm.util.merge.directive.provider.ProviderDeserializer;

/**
 *
 */
public class DefaultJsonProxy implements JsonProxy {

    protected GsonBuilder builder;

    public DefaultJsonProxy() {
        builder = new GsonBuilder();
        registerDefaultAdapters();
    }

    public DefaultJsonProxy(GsonBuilder builder) {
        this.builder = builder;
    }

    @Override
    public <T> T fromJSON(String jsonString, Class<T> type) {
        Gson gson = builder.create();
        return gson.fromJson(jsonString, type);
    }

    @Override
    public String toJson(Object content) {

        Gson gson = builder.create();
        return gson.toJson(content);
    }

    protected void registerDefaultAdapters() {
        builder.registerTypeAdapter(Directive.class, new DirectiveDeserializer());
        builder.registerTypeAdapter(Provider.class, new ProviderDeserializer());
    }
}
