package com.ibm.util.merge.web.rest.servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *
 */
public class RequestData {

    private final String method;
    private final byte[] requestBody;
    public String getMethod() {
		return method;
	}

	public byte[] getRequestBody() {
		return requestBody;
	}

	public Locale getPreferredLocale() {
		return preferredLocale;
	}

	public List<Locale> getLocales() {
		return locales;
	}

	public Map<String, String[]> getParams() {
		return params;
	}

	public String getPathInfo() {
		return pathInfo;
	}

	public String getQueryString() {
		return queryString;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public String getContentType() {
		return contentType;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	private final Locale preferredLocale;
    private final List<Locale> locales;
    private final Map<String, String[]> params;
    private final String pathInfo;
    private final String queryString;
    private final Map<String, List<String>> headers;
    private final String contentType;
    private final String requestUrl;

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
        requestUrl = request.getRequestURL().toString();
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

    public boolean isPUT() {
        return method.equalsIgnoreCase("put");
    }
    public boolean isPOST() {
        return method.equalsIgnoreCase("post");
    }

    public int requestBodyByteLength() {
        return requestBody.length;
    }

    public String getRequestBodyString() {
        try {
            return new String(requestBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String newUrlForChildPath(String childPath) {

        return requestUrl + childPath;
    }

    public Map<String, String[]> getParameterMap() {
        return params;
    }

    public boolean isDELETE() {
        return method.equalsIgnoreCase("delete");
    }
}
