package com.ibm.util.merge.web.rest.servlet.result;

import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.writer.TextResponseWriter;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class JsonDataResult implements Result {

    private static final Logger log = Logger.getLogger(JsonDataResult.class);

    private final String result;

    public JsonDataResult(String queryResult) {
        this.result = queryResult;
    }

    @Override
    public void write(RequestData rd, HttpServletRequest request, HttpServletResponse response) {
        if(result != null){
            response.setStatus(HttpServletResponse.SC_OK);
            String item = this.result;
            new TextResponseWriter(response, item).write();
        }else{
            log.warn("No result for " + rd);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
