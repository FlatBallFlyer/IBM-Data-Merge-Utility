package com.ibm.util.merge.json;

import com.google.gson.GsonBuilder;

/**
 * Generates pretty JSON
 */
public class PrettyJsonProxy extends DefaultJsonProxy {

    public PrettyJsonProxy() {
        super();
        enablePrettyPrinting();
    }

    public PrettyJsonProxy(GsonBuilder builder) {
        super(builder);
        enablePrettyPrinting();
    }

    private void enablePrettyPrinting() {
        builder.setPrettyPrinting();
    }


}
