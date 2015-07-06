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
package com.ibm.util.merge;

import com.ibm.util.merge.directive.Directive;
import com.ibm.util.merge.directive.provider.Provider;
import com.ibm.util.merge.json.PrettyJsonProxy;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Merge Processing Exception Class - This is the only exception thrown by the Merge Utility
 *
 * @author Mike Storey
 */
public class MergeException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MergeException.class.getName() );
	private String error;
	private String context;
	private Template template;
	private Directive directive;
	private Provider provider;
	private String errorFromClass;
	
	/**
	 * Constructor
	 * 
	 * @param message The exception message
	 * @param errorCode The error code
	 */
	public MergeException(Exception e, String errorMessage, String theContext){
		super(e);
		error = errorMessage;
		context = theContext;
		errorFromClass = "Wrapped: " + e.getClass().getName();
		logError();
	}
	
	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(String errorMessage, String theContext){
		super(errorMessage);
		error = errorMessage;
		context = theContext;
		logError();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(Template errTemplate, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		error = errorMessage;
		context = theContext;
		template = errTemplate;
		errorFromClass = errTemplate.getClass().getName();
		logError();
		logTemplate();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(Directive errDirective, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		error = errorMessage;
		context = theContext;
		directive = errDirective;
		template = directive.getTemplate();
		errorFromClass = errDirective.getClass().getName();
		logError();
		logTemplate();
		logDirective();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(Provider errProvider, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		error = errorMessage;
		context = theContext;
		provider = errProvider;
		directive = provider.getDirective();
		template = directive.getTemplate();
		errorFromClass = errProvider.getClass().getName();
		logError();
		logTemplate();
		logDirective();
		logProvider();
    }

	/**
	 * 
	 */
	private void logError() {
		log.fatal("Merge Exception: \n" +
			"Message: " + error + "\n" +
			"Context: " + context + "\n" +
			"StackTrace: ", this);
	}
	
	/**
	 * 
	 */
	private void logTemplate() {
		if (template != null) {
			log.fatal("Merge Exception: Template JSON \n" + new PrettyJsonProxy().toJson(template));
			log.fatal(template.getReplaceValues());
			log.fatal(template.getBookmarks());
		}
	}
	

	/**
	 * 
	 */
	private void logDirective() {
		if (directive != null ) {
			log.fatal("Merge Exception: Exception from Directive: " + Integer.toString(directive.getSequence()) );
		}
	}
	
	/**
	 * 
	 */
	private void logProvider() {
		if (provider != null ) {
			int size = provider.getTables().size();
			log.fatal("Merge Exception: Exception from Provider: " + Integer.toString(provider.getType()) + "\n" +
					"Query String: " + provider.getQueryString() + "\n" +
					"Data Tables: " + Integer.toString(size));
			if (size > 0) {
				log.fatal(provider.getTables());
			}
		}
	}
	
	/**
	 * @return
	 * @param rtc
	 */
	public String getHtmlErrorMessage(RuntimeContext rtc) {
		String message;
		Template errorTemplate;
		HashMap<String,String> parameters = new HashMap<>();
		parameters.put(Template.wrap("MESSAGE"), error);
		parameters.put(Template.wrap("CONTEXT"), context);
		parameters.put(Template.wrap("TRACE"), getStackTrace().toString());
		try {
			errorTemplate = rtc.getTemplateFactory().getTemplate("system.errHtml." + errorFromClass, "system.errHtml.", parameters);
			errorTemplate.merge(rtc);
			final String returnValue;
			if (!errorTemplate.canWrite()) {
                returnValue = "";
            } else {
                returnValue = errorTemplate.getContent();
            }
			errorTemplate.doWrite(rtc.getZipFactory());
			message = returnValue;
//			errorTemplate.packageOutput(zf, cf);
		} catch (MergeException e) {
			message = "INVALID ERROR TEMPLATE! \n" +
					"Message: " + error + "\n" +
					"Context: " + context + "\n";
		}
		return message;
	}

	/**
	 * @return
	 * @param rtc
	 */
	public String getJsonErrorMessage(RuntimeContext rtc) {
		String message;
		Template errorTemplate;
		HashMap<String,String> parameters = new HashMap<>();
		parameters.put(Template.wrap("MESSAGE"), error);
		parameters.put(Template.wrap("CONTEXT"), context);
		parameters.put(Template.wrap("TRACE"), getStackTrace().toString());
		try {
			errorTemplate = rtc.getTemplateFactory().getTemplate("system.errJson." + errorFromClass, "system.errJson.", parameters);
			errorTemplate.merge(rtc);
			final String returnValue;
			if (!errorTemplate.canWrite()) {
                returnValue = "";
            } else {
                returnValue = errorTemplate.getContent();
            }
			errorTemplate.doWrite(rtc.getZipFactory());
			message = returnValue;
//			errorTemplate.packageOutput(zf, cf);
		} catch (MergeException e) {
			message = "INVALID ERROR TEMPLATE! \n" +
					"Message: " + error + "\n" +
					"Context: " + context + "\n";
		}
		return message;
	}

	public String getMessage() {
		return super.getMessage() + " For: " + context;
	}
	
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
