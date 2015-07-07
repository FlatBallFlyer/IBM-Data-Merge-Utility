package com.ibm.util.merge.web.rest.servlet.result;

import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.writer.JsonResponseWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class JsonItemCreatedResult implements Result {
    private Object newItem;
    private String newItemUrl;

    public JsonItemCreatedResult(Object newItem, String newItemUrl) {
        this.newItem = newItem;
        this.newItemUrl = newItemUrl;
    }

    @Override
    public void write(RequestData rd, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.addHeader("Location", newItemUrl);
        new JsonResponseWriter(response, newItem)
                .write();
    }
}
