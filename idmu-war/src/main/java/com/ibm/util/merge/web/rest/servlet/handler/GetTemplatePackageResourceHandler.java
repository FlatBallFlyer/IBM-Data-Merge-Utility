package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.JsonDataResult;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;

/**
 * GET /idmu/templatePackage/{collectionName,collectionName...}
 */
public class GetTemplatePackageResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(GetTemplatePackageResourceHandler.class);

    private TemplateFactory tf;

    @Override
    public void initialize(Map<String, String> initParameters, TemplateFactory templateFactory) {
        this.tf = templateFactory;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isGET()) && rd.pathStartsWith("/templatePackage/") && rd.getPathParts().size() ==2;
    }

    @Override
    public Result handle(RequestData rd) {
        String collectionNames = rd.getPathParts().get(1);
        log.warn("get template collections " + collectionNames);
        ArrayList<String> names = new ArrayList<String>();
        for (String name : collectionNames.split(",")) {
        	names.add(name);
        }
    	return new JsonDataResult(tf.getTemplatesJSON(names));
    }

}
