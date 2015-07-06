package com.ibm.util.merge.web.rest.servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 *
 */
public class RequestData {

    private final String method;
    private final byte[] requestBody;
    private final Locale preferredLocale;
    private final List<Locale> locales;
    private final Map<String, String[]> params;
    private final String pathInfo;
    private final String queryString;
    private final Map<String, List<String>> headers;
    private final String contentType;

    public RequestData(HttpServletRequest request) {
        method = request.getMethod();
        byte[] bytes = readRequestBody(request);
        requestBody = bytes;
        contentType = request.getContentType();
        preferredLocale = request.getLocale();
        List<Locale> tlocales = getAllRequestLocales(request);
        locales = new ArrayList<>(tlocales);
        params = request.getParameterMap();
        pathInfo = request.getPathInfo();
        queryString = request.getQueryString();
        Map<String, List<String>> headerValues = readHeaderValues(request);
        headers = headerValues;
    }

    private Map<String, List<String>> readHeaderValues(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, List<String>> headerValues = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(headerName);
            List<String> vals = new ArrayList<>();
            while (values.hasMoreElements()) {
                String val = values.nextElement();
                vals.add(val);
            }
        }
        return headerValues;
    }

    private List<Locale> getAllRequestLocales(HttpServletRequest request) {
        Enumeration<Locale> allLocales = request.getLocales();
        List<Locale> tlocales = new ArrayList<>();
        while (allLocales.hasMoreElements()) {
            tlocales.add(allLocales.nextElement());
        }
        return tlocales;
    }

    private byte[] readRequestBody(HttpServletRequest request) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        try {
            IOUtils.copy(request.getInputStream(), bos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public String toString() {
        return "RequestData{" + method + " " + pathInfo + " qs=" + queryString + " paramCount=" + params.size() + " requestBodyLength=" + requestBody.length + "}";
    }

    public boolean isGET() {
        return method.equalsIgnoreCase("get");
    }

    public boolean pathEquals(String path) {
        return pathInfo.equals(path);
    }

    public List<String> getPathParts() {
        if(pathInfo.isEmpty()){
            return Collections.emptyList();
        }
        String[] parts = pathInfo.substring(1).split("/");
        return new ArrayList<>(Arrays.asList(parts));
    }

    public boolean pathStartsWith(String pathPrefix) {
        return pathInfo.startsWith(pathPrefix);
    }
}
