package com.ibm.util.merge.web;

import com.ibm.util.merge.web.rest.servlet.RequestData;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet only catches unmapped requests for this web application.
 */
public class CatchAllServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -959717532656133689L;
	private Logger log = Logger.getLogger(CatchAllServlet.class);
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestData rd = new RequestData(req);
        log.error("Unhandled request : " + rd);
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("This is not the URL you're looking for : " + req.getRequestURL());
    }
}
