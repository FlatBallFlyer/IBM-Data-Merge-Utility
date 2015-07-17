package com.ibm.util.merge.web.rest.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public interface Result {
    void write(RequestData rd, HttpServletRequest request, HttpServletResponse response);
}
