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
import com.ibm.util.merge.web.rest.servlet.result.PlainErrorResult;
import com.ibm.util.merge.web.rest.servlet.result.PlainResult;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Properties;

/**
 * GET /idmu/merge?DragonFlyFullName=fullname&{additional requestParameters}
 */
public class GetMergeOutputTextResourceHandler implements RequestHandler {
    private static final Logger log = Logger.getLogger(GetMergeOutputTextResourceHandler.class);
    private TemplateFactory tf;
    private String errTemplate;
    
    @Override
    public void initialize(Properties initParameters, TemplateFactory templateFactory) {
        this.tf = templateFactory;
        errTemplate = initParameters.getProperty("idmu.errorTemplate.GetMergeOutputText", "default");
    }

    @Override
    public boolean canHandle(RequestData rd) {
        return (rd.isGET()) && rd.pathStartsWith("/merge") && (rd.getPathParts().size() != 2);
    }

    @Override
    public Result handle(RequestData rd) {
    	Long start = System.currentTimeMillis();
    	HashMap<String, String[]> parameterMap = new HashMap<String, String[]>(rd.getParameterMap());
    	
    	// Perform the merge
    	try {
    		// Temporary implementation of content type and download file name
    		// This feature the will be migrated to new Template attributes in v4.X (Requires refactoring of TemplateFactory.getMergeOutput)
    		String filename = null;
    		String type = null;
    		String[] filenames = parameterMap.get("DragonFlyFileName");
    		if (filenames != null) {filename=filenames[0];}
    		
    		String[] types = parameterMap.get("DragonFlyFileType");
    		if (types != null) {type=types[0];}
    		
    		String result = tf.getMergeOutput(parameterMap);
	        long elapsed = System.currentTimeMillis() - start;
	        log.warn(String.format("Merge completed in %d milliseconds", elapsed));
	        return new PlainResult(result, type, filename);
    	} catch (MergeException e) {
    		return new PlainErrorResult(e, tf, errTemplate);
    	}
    }

}
