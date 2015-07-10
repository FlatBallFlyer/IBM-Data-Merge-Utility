package com.ibm.util.merge.web.rest.servlet.result;

import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Just set status 200 OK on the response
 */
public class OkResult implements Result {
    @Override
    public void write(RequestData rd, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
