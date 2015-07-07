package com.ibm.util.merge.web.rest.servlet.result;

import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.writer.JsonResponseWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class JsonItemUpdatedResult implements Result {
    private Object updatedItem;

    public JsonItemUpdatedResult(Object updatedItem) {
        this.updatedItem = updatedItem;
    }

    @Override
    public void write(RequestData rd, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        new JsonResponseWriter(response, updatedItem).write();
    }
}
