package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.result.JsonDataResult;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;

import java.util.List;
import java.util.Map;

/**
 * * Handles /templates
 */
public class AllTemplatesResourceHandler implements RequestHandler {
    private RuntimeContext runtimeContext;

    @Override
    public void initialize(Map<String, String> initParameters, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return rd.isGET() && rd.pathEquals("/templates");
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        List<Template> allTemplates = tf.listAllTemplates();
        return new JsonDataResult(allTemplates);
    }
}
