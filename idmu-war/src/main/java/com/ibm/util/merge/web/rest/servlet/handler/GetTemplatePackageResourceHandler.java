package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import org.apache.log4j.Logger;

import java.util.Arrays;
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
        String[] names = collectionNames.split(",");
        for (String collectionName : names) {
            log.info("TODO Load collection to add to package(s) download: " + collectionName);
        }
        throw new UnsupportedOperationException("TODO : return template package json for collections: " + Arrays.asList(names));
//        log.warn("getTemplates for " + collectionNames);
//        return new JsonDataResult(tf.getTemplateNamesJSON(collectionNames));
    }

}
