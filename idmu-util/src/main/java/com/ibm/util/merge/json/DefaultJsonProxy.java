package com.ibm.util.merge.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.directive.AbstractDirective;
import com.ibm.util.merge.directive.provider.AbstractProvider;

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
        builder.registerTypeAdapter(AbstractDirective.class, new DirectiveDeserializer());
        builder.registerTypeAdapter(AbstractProvider.class, new ProviderDeserializer());
    }
}
