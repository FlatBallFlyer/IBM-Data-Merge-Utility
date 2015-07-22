package com.ibm.util.merge.web;

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class RestServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(RestServlet.class);


    public static MergeContext findRuntimeContext(ServletContext servletContext) {
        MergeContext rtc = (MergeContext) servletContext.getAttribute("rtc");
        if(rtc == null){
            throw new IllegalStateException("Could not find MergeContext attribute 'rtc' in ServletContext");
        }
        return rtc;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        log.info("Initializing with servletConfig: " + servletConfig);
        MergeContext rtc = findRuntimeContext(servletConfig.getServletContext());
        log.info("RTC=" + rtc);
        List<RequestHandler> handlerChain = findHandlerChain(servletConfig.getServletContext());
        log.info("handlerChain=" + handlerChain);
    }

    public static List<RequestHandler> findHandlerChain(ServletContext servletContext) {
        return (List<RequestHandler>) servletContext.getAttribute("handlerChain");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) {
        RequestData rd = new RequestData(request);
        logRequest(rd);
        boolean handled = false;
        for (RequestHandler handler : handlerChain()) {
            boolean canHandle = handler.canHandle(rd);
            log.info("Handler " + handler.getClass().getSimpleName() + "? " + canHandle);
            if(canHandle){
                if(handled) throw new IllegalStateException("Multiple handlers match for " + rd);
                handled = true;
                Result result = handler.handle(rd);
                result.write(rd, request, response);
                if(response.getStatus() >= 200 && response.getStatus() < 300){
                    log.info("Successfully handled request with " + handler.getClass().getName() + " : " + rd);
                }else{
                    log.error("Failed to handle request with " + handler.getClass().getName() + " : " + rd);
                }
            }
        }
        if(!handled){
            throw new RuntimeException("Unhandled : " + rd);
        }
    }

    private List<RequestHandler> handlerChain() {
        return findHandlerChain(getServletContext());
    }

    private void logRequest(RequestData rd) {
        log.info(rd.toString());
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response);
    }

    public static Map<String, String> initParametersToMap(ServletConfig cfg) {
        Enumeration<String> names = cfg.getInitParameterNames();
        HashMap<String, String> out = new HashMap<>();
        while(names.hasMoreElements()){
            String name = names.nextElement();
            out.put(name, cfg.getInitParameter(name));
        }
        return out;
    }
}
