package com.ibm.util.merge.web.rest.servlet.result;

import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.Result;
import org.apache.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class JsonQueryResult implements Result {

    private static final Logger log = Logger.getLogger(JsonQueryResult.class);

    private final Object result;

    public JsonQueryResult(Object queryResult) {
        this.result = queryResult;
    }

    @Override
    public void write(RequestData rd, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if(result != null){
            response.setStatus(HttpServletResponse.SC_OK);
            try {
                ServletOutputStream os = response.getOutputStream();
                os.write(new DefaultJsonProxy().toJson(result).getBytes("UTF-8"));
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            log.warn("No result for " + rd);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }


    }
}
