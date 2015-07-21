package com.ibm.util.merge.web.rest.servlet;

import com.ibm.util.merge.TemplateFactory;

import java.util.Map;

/**
 *
 */
public interface RequestHandler {

    void initialize(Map<String, String> initParameters, TemplateFactory tf);

    boolean canHandle(RequestData rd);

    Result handle(RequestData rd);
}
