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

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.web.rest.servlet.RequestData;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.Result;
import com.ibm.util.merge.web.rest.servlet.result.HtmlErrorResult;
import com.ibm.util.merge.web.rest.servlet.result.JsonResult;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * POST /idmu/templatePackage/{filename}
 */
public class PostTemplatePackageResourceHandler implements RequestHandler {
    private static final Logger log = Logger.getLogger(PostTemplatePackageResourceHandler.class);
    private TemplateFactory tf;
    private String errTemplate;

    @Override
    public void initialize(Properties initParameters, TemplateFactory templateFactory) {
        this.tf = templateFactory;
        errTemplate = initParameters.getProperty("idmu.errorTemplate." + this.getClass().toString(), "default");
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isPOST()) && rd.pathStartsWith("/templatePackage/") && rd.getPathParts().size() == 2;
    }

    @Override
    public Result handle(RequestData rd) {
        String packageName = rd.getPathParts().get(1);
        log.warn("Post Template Package " + packageName);
		try {
	        return new JsonResult(tf.loadPackage(packageName));
		} catch (MergeException e) {
	        return new HtmlErrorResult(e, tf, errTemplate);
		}
    }

}
