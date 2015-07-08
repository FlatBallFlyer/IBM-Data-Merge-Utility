package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.ForbiddenRequestTextResult;
import com.ibm.util.merge.web.rest.servlet.result.JsonItemCreatedResult;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Handles POST /template
 */
public class CreateTemplateResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(CreateTemplateResourceHandler.class);

    private RuntimeContext runtimeContext;

    @Override
    public void initialize(Map<String, String> initParameters, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return rd.isPOST() && rd.pathEquals("/template");
    }

    @Override
    public Result handle(RequestData rd) {
        TemplateFactory tf = runtimeContext.getTemplateFactory();

        String requestBodyString = rd.getRequestBodyString();
        log.info("CREATE template from " + requestBodyString);
        Template template = new DefaultJsonProxy().fromJSON(requestBodyString, Template.class);
        String fullName = template.getFullName();
        Result result;
        if(tf.templateFullnameExists(fullName)){
            result = new ForbiddenRequestTextResult("Cannot create, there already is a template with fullName="+fullName);
        }else{
            tf.persistTemplate(template);
            Template newTemplateInstance = tf.cache(template);
            String newItemUrl = rd.newUrlForChildPath("/" + fullName);
            result = new JsonItemCreatedResult(newTemplateInstance, newItemUrl);
        }
        return result;
    }
}
