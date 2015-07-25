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
package com.ibm.util.merge.web.rest.servlet.writer;

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
