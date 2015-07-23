package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.JsonDataResult;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Handles POST /merge
 */
public class GetTemplatesResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(GetTemplatesResourceHandler.class);

    private TemplateFactory tf;

    @Override
    public void initialize(Map<String, String> initParameters, TemplateFactory templateFactory) {
        this.tf = templateFactory;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isGET()) && rd.pathEquals("/templates");
    }

    @Override
    public Result handle(RequestData rd) {
        String collection = rd.getPathParts().get(1);
        log.warn("getTemplates for " + collection);
        return new JsonDataResult(tf.getTemplateNamesJSON(collection));
    }

}