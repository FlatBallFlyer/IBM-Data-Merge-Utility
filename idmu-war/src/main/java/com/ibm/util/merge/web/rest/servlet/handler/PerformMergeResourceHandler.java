package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.*;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.MergeResult;
import org.apache.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        Template root = runtimeContext.getTemplateFactory().getTemplate(rd.getParameterMap());
        long start = System.currentTimeMillis();

        try {
            root.merge(runtimeContext);
        } catch (MergeException e) {
            String htmlErrorMessage = runtimeContext.getHtmlErrorMessage(e);
            log.error("Could not merge " + rd, e);
            return new HtmlResult(htmlErrorMessage);

        }
        if (root.canWrite()) {
            runtimeContext.getTemplateFactory().getFs().doWrite(root);

        }
        long elapsed = System.currentTimeMillis() - start;
        log.warn(String.format("Merge completed in %d milliseconds", elapsed));
        return new MergeResult(root.getContent());
    }

    public static class HtmlResult implements Result {
        private final String html;

        public HtmlResult(String htmlErrorMessage) {
            this.html = htmlErrorMessage;
        }

        @Override
        public void write(RequestData rd, HttpServletRequest request, HttpServletResponse response) {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            try {
                ServletOutputStream os = response.getOutputStream();
                os.write(this.html.getBytes("UTF-8"));
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
