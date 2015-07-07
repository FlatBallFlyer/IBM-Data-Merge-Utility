package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.result.JsonDataResult;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Handles /template/{templateFullName}
 */
public class TemplateForFullnameResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(TemplateForFullnameResourceHandler.class);

    private RuntimeContext runtimeContext;

    @Override
    public void initialize(Map<String, String> initParameters, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        List<String> pathParts = rd.getPathParts();
        return rd.isGET() && rd.pathStartsWith("/template/") && pathParts.size() == 2;
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        String fullName= rd.getPathParts().get(1);
        log.info("Listing templates for fullName=" + fullName);
        List<Template> collectionTemplates = tf.listAllTemplates();
        Template found = null;
        for (Template template : collectionTemplates) {
            if(template.getFullName().equals(fullName)){
                if(found != null){
                    throw new IllegalStateException("Duplicate template found for fullName=" + fullName);
                }
                found = template;
            }
        }
        return new JsonDataResult(found);
    }
}
