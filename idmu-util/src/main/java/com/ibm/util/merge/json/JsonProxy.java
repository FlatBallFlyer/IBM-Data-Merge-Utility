package com.ibm.util.merge.json;

/**
 *
 */
public interface JsonProxy {
    <T> T fromJSON(String jsonString, Class<T> type);
    String toJson(Object content);
}
