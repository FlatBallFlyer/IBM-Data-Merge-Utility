/*
 * 
 * Copyright 2015-2017 IBM
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
package com.ibm.util.merge.exception;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.AbstractDirective;

/**
 *  Custom Exception abstract base class - provides customized error messaging
 * @author Mike Storey
 *
 */
public abstract class MergeException extends Exception {
	private static final Logger LOGGER = Logger.getLogger(MergeException.class.getName());
	
	private static final long serialVersionUID = 9055769776571167065L;
	private static final String IDMU_EXCEPTION 				= "idmuException";
	private static final String IDMU_EXCEPTION_DATA 			= "idmuExceptionData";
	private static final String IDMU_STACK_TRACE 			= "idmuStackTrace";
	private static final String IDMU_ERROR_MSG	 			= "idmuErrorMessage";

	private String type;
	private String error;
	private Template template = null;
	private AbstractDirective directive = null;
	
	public MergeException(String error) {
		super(error);
		this.error = error;
	}
	
	public String getErrorMessage() {
		return error;
	}
	
	public String getErrorMessage(Merger context) {
		DataProxyJson proxy = new DataProxyJson(context.getConfig().isPrettyJson());
	    
	    String specific = "system." + this.getType() + "." + 
	    		this.getStackTrace()[0].getClassName() + "." +
	    		this.getStackTrace()[0].getMethodName();
		String general = "system." + this.getType() + ".";
		
		String stacktrace = ExceptionUtils.getStackTrace(this);
		
		try {
			context.getMergeData().put(IDMU_EXCEPTION_DATA, "-", proxy.toString(context.getMergeData()));
			context.getMergeData().put(IDMU_EXCEPTION, "-", 		proxy.toString(this));
			context.getMergeData().put(IDMU_STACK_TRACE, "-", 	stacktrace);
			context.getMergeData().put(IDMU_ERROR_MSG, "-", 		this.error);
			
			Template errorMessage = context.getMergable(specific, general, new HashMap<String,String>());
			return errorMessage.getMergedOutput().getValue();
		} catch (Throwable e) {
			try {
				LOGGER.log(Level.SEVERE, "Exception Caught in system error templates - Reloading System Error Templates!");
				context.getCahce().buildDefaultSystemTemplates();
				Template errorMessage = context.getMergable(specific, general, new HashMap<String,String>());
				return errorMessage.getMergedOutput().getValue();
			} catch (Throwable t) {
				return this.getMessage().concat(" - pigs flying:").concat(t.getMessage());
			}
		}
	}
	
	public void setTemplate(Template theTemplate) {
		if (null == this.template) {
			this.template = theTemplate;
		}
	}
	
	public void setDirective(AbstractDirective theDirective) {
		if (null == this.directive) {
			this.directive = theDirective;
		}
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
