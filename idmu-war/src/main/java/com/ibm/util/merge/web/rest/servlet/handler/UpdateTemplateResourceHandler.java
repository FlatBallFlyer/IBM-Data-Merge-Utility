package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.JsonItemUpdatedResult;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles PUT /templates/{collectionName}
 */
public class UpdateTemplateResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(UpdateTemplateResourceHandler.class);

    private RuntimeContext runtimeContext;

    @Override
    public void initialize(Map<String, String> initParameters, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return rd.isPUT() && rd.pathStartsWith("/templates/") && rd.getPathParts().size() == 2 && rd.requestBodyByteLength() > 10;
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        String collectionName= rd.getPathParts().get(1);
        Template template = new DefaultJsonProxy().fromJSON(rd.getRequestBodyString(), Template.class);
        template.setCollection(collectionName);
        Template found = tf.findTemplateForFullname(template.getFullName());
        Result result;
        if(found == null){
            log.info("Creating template " + template.getFullName() + " from " + rd.getRequestBodyString());
            tf.persistTemplate(template);
            Template newTemplateInstance = tf.cache(template);
            result = new JsonItemUpdatedResult(newTemplateInstance);
        }else{
            log.info("Updating template " + template.getFullName() + " with " + rd.getRequestBodyString());
            tf.persistTemplate(template);
            Template newTemplateInstance = tf.cache(template);
            result = new JsonItemUpdatedResult(newTemplateInstance);
        }
        return result;
    }
}
