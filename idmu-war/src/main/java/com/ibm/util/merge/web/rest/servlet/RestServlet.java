package com.ibm.util.merge.web.rest.servlet;

import com.ibm.util.merge.RuntimeContext;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 */
public class RestServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(RestServlet.class);


    public static RuntimeContext findRuntimeContext(ServletContext servletContext) {
        RuntimeContext rtc = (RuntimeContext) servletContext.getAttribute("rtc");
        if(rtc == null){
            throw new IllegalStateException("Could not find RuntimeContext attribute 'rtc' in ServletContext");
        }
        return rtc;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        log.info("Initializing with servletConfig: " + servletConfig);
        RuntimeContext rtc = findRuntimeContext(servletConfig.getServletContext());
        log.info("RTC=" + rtc);
        List<RequestHandler> handlerChain = findHandlerChain(servletConfig.getServletContext());
        log.info("handlerChain=" + handlerChain);
    }

    public static List<RequestHandler> findHandlerChain(ServletContext servletContext) {
        return (List<RequestHandler>) servletContext.getAttribute("handlerChain");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        logRequest(request);
        RequestData rd = new RequestData(request);
        boolean handled = false;
        for (RequestHandler handler : handlerChain()) {
            if(handler.canHandle(rd)){
                if(handled) throw new IllegalStateException("Multiple handlers match for " + rd);
                handled = true;
                Result result = handler.handle(rd);
                result.write(rd, request, response);
            }
        }
        if(!handled){
            throw new RuntimeException("Unhandled : " + rd);
        }
    }

    private List<RequestHandler> handlerChain() {
        return findHandlerChain(getServletContext());
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
