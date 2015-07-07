package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.*;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.MergeResult;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Handles POST /merge
 */
public class PerformMergeResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(PerformMergeResourceHandler.class);

    private RuntimeContext runtimeContext;

    @Override
    public void initialize(Map<String, String> initParameters, RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isPOST() || rd.isGET()) && rd.pathEquals("/merge");
    }

    @Override
    public Result handle(RequestData rd) {

//        TemplateFactory tf = runtimeContext.getTemplateFactory();

//        String requestBodyString = rd.getRequestBodyString();
//        log.info("CREATE template from " + requestBodyString);
//        Template template = new DefaultJsonProxy().fromJSON(requestBodyString, Template.class);
//        String fullName = template.getFullName();
//        Result result;
//        if(tf.templateFullnameExists(fullName)){
//            result = new ForbiddenRequestTextResult("Cannot create, there already is a template with fullName="+fullName);
//        }else{
//            tf.persistTemplate(template);
//            Template newTemplateInstance = tf.cache(template);
//            String newItemUrl = rd.newUrlForChildPath("/" + fullName);
//            result = new JsonItemCreatedResult(newTemplateInstance, newItemUrl);
//        }
        Template root = runtimeContext.getTemplateFactory().getTemplate(rd.getParameterMap());
        long start = System.currentTimeMillis();
        // Create the response writer
//        response.setContentType("text/html");
//        PrintWriter out = response.getWriter();
        // Get a template using the httpServletRequest constructor
        // Perform the merge and write output

        try {
            root.merge(runtimeContext);
        } catch (MergeException e) {
            throw new RuntimeException("Could not merge " + rd, e);
        }
//        final String returnValue;
        if (!root.canWrite()) {
//            returnValue = "";
        } else {
            //            returnValue = root.getContent();
            root.doWrite(runtimeContext.getZipFactory());
        }

//        out.write(root.getContent());
        // Close Connections and Finalize Output
//        root.packageOutput(rtc.getZipFactory(), rtc.getConnectionFactory());
        long elapsed = System.currentTimeMillis() - start;
        log.warn(String.format("Merge completed in %d milliseconds", elapsed));
        return new MergeResult(root.getContent());
    }
}
