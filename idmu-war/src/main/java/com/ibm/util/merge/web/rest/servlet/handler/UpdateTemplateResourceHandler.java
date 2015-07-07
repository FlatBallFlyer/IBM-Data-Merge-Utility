package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.JsonItemUpdatedResult;
import com.ibm.util.merge.web.rest.servlet.result.NotFoundTextErrorResult;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Handles PUT /template/{templateFullName}
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
        List<String> pathParts = rd.getPathParts();
        return rd.isPUT() && rd.pathStartsWith("/template/") && pathParts.size() == 2 && rd.requestBodyByteLength() > 10;
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        String fullName= rd.getPathParts().get(1);
        log.info("Listing templates for fullName=" + fullName);
        Template found = tf.findTemplateForFullname(fullName);
        Result result;
        if(found == null){
            result = new NotFoundTextErrorResult("There is not template with name " + fullName + " to update");
        }else{
            String requestBodyString = rd.getRequestBodyString();
            log.info("UPDATE template " + fullName + " with " + requestBodyString);
            Template template = new DefaultJsonProxy().fromJSON(requestBodyString, Template.class);
            tf.persistTemplate(template);
            Template newTemplateInstance = tf.cache(template);
            result = new JsonItemUpdatedResult(newTemplateInstance);
        }
        return result;
    }
}
