package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * PUT /idmu/templatePackage/{Templates}
 */
public class PutTemplatePackageResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(PutTemplatePackageResourceHandler.class);

    private TemplateFactory tf;

    @Override
    public void initialize(Map<String, String> initParameters, TemplateFactory templateFactory) {
        this.tf = templateFactory;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isPUT()) && rd.pathEquals("/templatePackage");
    }

    @Override
    public Result handle(RequestData rd) {
        String potentiallyMultiplePackagesJSON = rd.getRequestBodyString();
        throw new UnsupportedOperationException("Save uploaded template package(s) for JSON : " + potentiallyMultiplePackagesJSON);
//        return new JsonDataResult(tf.saveTemplateFromJson(template));
    }

}
