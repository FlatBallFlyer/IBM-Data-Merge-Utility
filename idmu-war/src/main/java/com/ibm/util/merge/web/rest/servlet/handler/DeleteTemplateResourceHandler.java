package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.NotFoundTextErrorResult;
import com.ibm.util.merge.web.rest.servlet.result.OkResult;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Handles DELETE /template/{templateFullName}
 */
public class DeleteTemplateResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(DeleteTemplateResourceHandler.class);

    private RuntimeContext runtimeContext;

    @Override
    public void initialize(Map<String, String> initParameters, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        List<String> pathParts = rd.getPathParts();
        return rd.isDELETE() && rd.pathStartsWith("/template/") && pathParts.size() == 2;
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        String fullName= rd.getPathParts().get(1);
        log.info("DELETING template " + fullName);
        Template template = tf.findTemplateForFullname(fullName);
        Result result;
        if(template == null){
            String message = "There is not template with name " + fullName + " to delete";
            log.error(message);
            result = new NotFoundTextErrorResult(message);
        }else{
            tf.deleteTemplate(template);
            log.info("DELETED template " + fullName);
            result = new OkResult();
        }
        return result;
    }
}
