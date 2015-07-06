package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.JsonQueryResult;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles /templates/{collectionName}/{templateType}
 */
public class CollectionTemplatesForTypeResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(CollectionTemplatesForTypeResourceHandler.class);

    private RuntimeContext runtimeContext;

    @Override
    public void initialize(ServletConfig servletConfig, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        List<String> pathParts = rd.getPathParts();
        return rd.isGET() && rd.pathStartsWith("/templates/") && pathParts.size() == 3;
    }

    @Override
    public Result handle(RequestData rd, HttpServletRequest request) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        String collectionName = rd.getPathParts().get(1);
        String templateName = rd.getPathParts().get(2);
        log.info("Listing templates for collection=" + collectionName + " name="+templateName);
        List<Template> collectionTemplates = tf.mapTemplatesPerCollection().get(collectionName);
        List<Template> templatesWithName = new ArrayList<>();
        for (Template template : collectionTemplates) {
            if(template.getName().equals(templateName)){
                templatesWithName.add(template);
            }
        }
        return new JsonQueryResult(templatesWithName);
    }
}
