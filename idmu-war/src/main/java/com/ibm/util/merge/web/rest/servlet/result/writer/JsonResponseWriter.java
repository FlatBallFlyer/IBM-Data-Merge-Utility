package com.ibm.util.merge.web.rest.servlet.result.writer;

import com.ibm.util.merge.json.DefaultJsonProxy;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class JsonResponseWriter {
    private HttpServletResponse response;
    private Object item;

    public JsonResponseWriter(HttpServletResponse response, Object item) {
        this.response = response;
        this.item = item;
    }

    public void write() {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ServletOutputStream os = response.getOutputStream();
            os.write(new DefaultJsonProxy().toJson(item).getBytes("UTF-8"));
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
