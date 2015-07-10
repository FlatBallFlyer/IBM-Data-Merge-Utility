package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.directive.AbstractDirective;
import com.ibm.util.merge.web.rest.servlet.result.JsonDataResult;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * * Handles /directives
 */
public class AllDirectivesResourceHandler implements RequestHandler {
    private RuntimeContext runtimeContext;

    @Override
    public void initialize(Map<String, String> initParameters, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return rd.isGET() && rd.pathEquals("/directives");
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        List<Template> allTemplates = tf.listAllTemplates();
        List<AbstractDirective> allDirectives = new ArrayList<>();
        for (Template template : allTemplates) {
            List<AbstractDirective> direct = template.getDirectives();
            allDirectives.addAll(direct);
        }
        return new JsonDataResult(allDirectives);
    }
}
