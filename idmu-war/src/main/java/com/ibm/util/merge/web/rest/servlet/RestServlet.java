package com.ibm.util.merge.web.rest.servlet;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.web.rest.servlet.handler.*;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class RestServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(RestServlet.class);
    private RuntimeContext rtc;

    private List<RequestHandler> handlerChain = new ArrayList<>(Arrays.asList(
            new AllTemplatesResourceHandler(),
            new AllDirectivesResourceHandler(),
            new CollectionTemplatesResourceHandler(),
            new CollectionTemplatesForTypeResourceHandler(),
            new CollectionForCollectionTypeColumnValueResourceHandler(),
            new TemplateForFullnameResourceHandler()
    ));

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        log.info("Initializing with servletConfig: " + servletConfig);
        rtc = (RuntimeContext) servletConfig.getServletContext().getAttribute("rtc");
        for (RequestHandler handler : handlerChain) {
            handler.initialize(servletConfig, rtc);
        }
        log.info("RTC=" + rtc);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        logRequest(request);
        RequestData rd = new RequestData(request);
        boolean handled = false;
        for (RequestHandler handler : handlerChain) {
            if(handler.canHandle(rd)){
                if(handled) throw new IllegalStateException("Multiple handlers match for " + rd);
                handled = true;
                Result result = handler.handle(rd, request);
                result.write(rd, request, response);
            }
        }
        if(!handled){
            throw new RuntimeException("Unhandled : " + rd);
        }
    }

    private void logRequest(HttpServletRequest request) {
        log.info(new RequestData(request).toString());
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) {
        logRequest(request);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        logRequest(request);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        logRequest(request);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        logRequest(request);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        logRequest(request);
    }
}
