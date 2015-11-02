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

import java.util.HashMap;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.writer.TextResponseWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 404 Not found with a plain text message
 */
public class PlainErrorResult implements Result {
    private MergeException exception;
    private String errTemplate;
    private TemplateFactory factory;

    public PlainErrorResult(String message, TemplateFactory tf, String errTemplate) {
        this.exception = new MergeException(message, "", null);
        this.errTemplate = "error." + errTemplate + ".";
        this.factory = tf;
    }

    public PlainErrorResult(MergeException exception, TemplateFactory tf, String errTemplate) {
        this.exception = exception;
        this.errTemplate = "error." + errTemplate + ".";
        this.factory = tf;
    }

    @Override
    public void write(RequestData rd, HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        String message;
		try {
			HashMap<String,String[]> parameters = new HashMap<String,String[]>();
			parameters.put("DragonFlyFullName",  new String[]{errTemplate + exception.getErrorFromClass()});
			parameters.put("DragonFlyShortName", new String[]{errTemplate});
			parameters.putAll(exception.getParameters());
			message = this.factory.getMergeOutput(parameters);
		} catch (MergeException e) {
            message = "INVALID ERROR TEMPLATE! \n" +
                    "Message: " + exception.getError() + "\n" +
                    "Context: " + exception.getContext() + "\n";
		}
        new TextResponseWriter(resp, message).write();
    }
}
