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
package com.ibm.util.merge.web.rest.servlet.handler;

import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.JsonResult;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * GET /idmu/template/{templateFullName}
 */
public class GetTemplateNamesForNameResourceHandler implements RequestHandler {

    private static final Logger log = Logger.getLogger(GetTemplateNamesForNameResourceHandler.class);

    private TemplateFactory tf;

    @Override
    public void initialize(Properties initParameters, TemplateFactory templateFactory) {
        this.tf = templateFactory;
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isGET()) && rd.pathStartsWith("/templates/") && rd.getPathParts().size() == 3;
    }

    @Override
    public Result handle(RequestData rd) {
        String collectionName = rd.getPathParts().get(1);
        String templateName = rd.getPathParts().get(2);
        log.warn("get templates " + collectionName + "." + templateName);
    	return new JsonResult(tf.getTemplateNamesJSON(collectionName, templateName));
    }

}
