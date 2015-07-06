package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.JsonQueryResult;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * * Handles /templates
 */
public class AllTemplatesResourceHandler implements RequestHandler {
    private RuntimeContext runtimeContext;

    @Override
    public void initialize(ServletConfig servletConfig, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return rd.isGET() && rd.pathEquals("/templates");
    }

    @Override
    public Result handle(RequestData rd, HttpServletRequest request) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        List<Template> allTemplates = tf.listAllTemplates();
        return new JsonQueryResult(allTemplates);
    }
}
