package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.MergeResult;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * GET /idmu/merge?DragonFlyFullName=fullname&{additional requestParameters}
 */
public class PerformMergeResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(PerformMergeResourceHandler.class);

    private TemplateFactory tf;

    @Override
    public void initialize(Map<String, String> initParameters, TemplateFactory templateFactory) {
        this.tf = templateFactory;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isGET()) && rd.pathEquals("/merge");
    }

    @Override
    public Result handle(RequestData rd) {
    	Long start = System.currentTimeMillis();
        String result = tf.getMergeOutput(rd.getParameterMap());
        long elapsed = System.currentTimeMillis() - start;
        log.warn(String.format("Merge completed in %d milliseconds", elapsed));
        return new MergeResult(result);
    }

}
