package com.ibm.util.merge.web.rest.servlet;

import com.ibm.util.merge.RuntimeContext;

import java.util.Map;

/**
 *
 */
public interface RequestHandler {

    void initialize(Map<String, String> initParameters, RuntimeContext runtimeContext);

    boolean canHandle(RequestData rd);

    Result handle(RequestData rd);
}
