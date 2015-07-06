package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.directive.Directive;
import com.ibm.util.merge.web.rest.servlet.result.JsonQueryResult;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;

import javax.servlet.ServletConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * * Handles /directives
 */
public class AllDirectivesResourceHandler implements RequestHandler {
    private RuntimeContext runtimeContext;

    @Override
    public void initialize(ServletConfig servletConfig, RuntimeContext runtimeContext) {
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
        List<Directive> allDirectives = new ArrayList<>();
        for (Template template : allTemplates) {
            List<Directive> direct = template.getDirectives();
            allDirectives.addAll(direct);
        }
        return new JsonQueryResult(allDirectives);
    }
}
