package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.result.JsonDataResult;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import java.util.List;

/**
 * Handles /templates/{collectionName}
 */
public class CollectionTemplatesResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(CollectionTemplatesResourceHandler.class);

    private RuntimeContext runtimeContext;

    @Override
    public void initialize(ServletConfig servletConfig, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        List<String> pathParts = rd.getPathParts();
        return rd.isGET() && rd.pathStartsWith("/templates/") && pathParts.size() == 2;
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        String collectionName = rd.getPathParts().get(1);
        log.info("Listing templates for collection " + collectionName);
        List<Template> allTemplates = tf.mapTemplatesPerCollection().get(collectionName);
        return new JsonDataResult(allTemplates);
    }
}
