package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.result.JsonQueryResult;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import java.util.List;

/**
 * Handles /templates/{collectionName}/{templateType}/{columnValue}
 */
public class CollectionForCollectionTypeColumnValueResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(CollectionForCollectionTypeColumnValueResourceHandler.class);

    private RuntimeContext runtimeContext;

    @Override
    public void initialize(ServletConfig servletConfig, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        List<String> pathParts = rd.getPathParts();
        return rd.isGET() && rd.pathStartsWith("/templates/") && pathParts.size() == 4;
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();
        String collectionName = rd.getPathParts().get(1);
        String templateName = rd.getPathParts().get(2);
        String columnValue = rd.getPathParts().get(3);
        log.info("Listing templates for collection=" + collectionName + " name="+templateName + " columnValue="+columnValue);
        List<Template> collectionTemplates = tf.mapTemplatesPerCollection().get(collectionName);
        Template found = null;
        for (Template template : collectionTemplates) {
            if(template.getName().equals(templateName) && template.getColumnValue().equals(columnValue)){
                if(found != null){
                    throw new IllegalStateException("Already found");
                }
                found = template;
            }
        }
        return new JsonQueryResult(found);
    }
}
