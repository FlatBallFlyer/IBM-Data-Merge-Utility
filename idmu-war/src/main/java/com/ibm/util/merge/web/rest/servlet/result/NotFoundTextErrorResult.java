package com.ibm.util.merge.web.rest.servlet.result;

import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.writer.TextResponseWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 404 Not found with a plain text message
 */
public class NotFoundTextErrorResult implements Result {
    private String message;

    public NotFoundTextErrorResult(String message) {
        this.message = message;
    }

    @Override
    public void write(RequestData rd, HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        new TextResponseWriter(resp, this.message).write();
    }
}
