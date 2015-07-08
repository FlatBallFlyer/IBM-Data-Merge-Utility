package com.ibm.idmu.api;

/**
 *
 */
public interface JsonProxy {
    <T> T fromJSON(String jsonString, Class<T> type);
    String toJson(Object content);
}
