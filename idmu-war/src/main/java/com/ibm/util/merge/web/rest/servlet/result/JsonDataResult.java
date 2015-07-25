/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
