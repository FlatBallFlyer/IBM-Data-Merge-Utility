package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.JsonDataResult;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * GET /idmu/template/{templateFullName}
 */
public class GetCollectionTemplatesWithNameResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(GetCollectionTemplatesWithNameResourceHandler.class);

    private TemplateFactory tf;

    @Override
    public void initialize(Map<String, String> initParameters, TemplateFactory templateFactory) {
        this.tf = templateFactory;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isGET()) && rd.pathStartsWith("/templates/") && rd.getPathParts().size() == 3;
    }

    @Override
    public Result handle(RequestData rd) {
        String collectionName = rd.getPathParts().get(1);
        String templateName = rd.getPathParts().get(2);
        throw new UnsupportedOperationException("TODO list templates for collection " + collectionName + " that have name " + templateName);
//        return new JsonDataResult(tf.getTemplateAsJson(fullName));
    }

}
