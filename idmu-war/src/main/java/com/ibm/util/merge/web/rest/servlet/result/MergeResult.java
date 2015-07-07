package com.ibm.util.merge.web.rest.servlet.result;

import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class MergeResult implements Result {
    private String content;

    public MergeResult(String content) {
        this.content = content;
    }

    @Override
    public void write(RequestData rd, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
