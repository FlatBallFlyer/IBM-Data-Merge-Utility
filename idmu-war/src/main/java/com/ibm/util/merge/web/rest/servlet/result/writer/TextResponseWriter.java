package com.ibm.util.merge.web.rest.servlet.result.writer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class TextResponseWriter {
    private HttpServletResponse resp;
    private String message;

    public TextResponseWriter(HttpServletResponse resp, String message) {
        this.resp = resp;
        this.message = message;
    }

    public void write() {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        try {
            PrintWriter w = resp.getWriter();
            w.write(message);
            w.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
