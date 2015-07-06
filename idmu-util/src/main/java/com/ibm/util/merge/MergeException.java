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
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ibm.util.merge.directive.Directive;
import com.ibm.util.merge.directive.provider.Provider;

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
		this.error = errorMessage;
		this.context = theContext;
		this.errorFromClass = "Wrapped: " + e.getClass().getName();
		this.logError();
	}
	
	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(String errorMessage, String theContext){
		super(errorMessage);
		this.error = errorMessage;
		this.context = theContext;
		this.logError();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(Template errTemplate, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		this.error = errorMessage;
		this.context = theContext;
		this.template = errTemplate;
		this.errorFromClass = errTemplate.getClass().getName();
		
		this.logError();
		this.logTemplate();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(Directive errDirective, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		this.error = errorMessage;
		this.context = theContext;
		this.directive = errDirective;
		this.template = this.directive.getTemplate();
		this.errorFromClass = errDirective.getClass().getName();
		
		this.logError();
		this.logTemplate();
		this.logDirective();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(Provider errProvider, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		this.error = errorMessage;
		this.context = theContext;
		this.provider = errProvider;
		this.directive = this.provider.getDirective();
		this.template = this.directive.getTemplate();
		this.errorFromClass = errProvider.getClass().getName();
		
		this.logError();
		this.logTemplate();
		this.logDirective();
		this.logProvider();
    }

	/**
	 * 
	 */
	private void logError() {
		log.fatal("Merge Exception: \n" +
			"Message: " + this.error + "\n" +
			"Context: " + this.context + "\n" + 
			"StackTrace: ", this);
	}
	
	/**
	 * 
	 */
	private void logTemplate() {
		if (this.template != null) {
			log.fatal("Merge Exception: Template JSON \n" + this.template.asJson(true));
			log.fatal(this.template.getReplaceValues());
			log.fatal(this.template.getBookmarks());
		}
	}
	

	/**
	 * 
	 */
	private void logDirective() {
		if (this.directive != null ) {
			log.fatal("Merge Exception: Exception from Directive: " + Integer.toString(this.directive.getSequence()) );
		}
	}
	
	/**
	 * 
	 */
	private void logProvider() {
		if ( this.provider != null ) {
			int size = this.provider.getTables().size();
			log.fatal("Merge Exception: Exception from Provider: " + Integer.toString(this.provider.getType()) + "\n" + 
					"Query String: " + this.provider.getQueryString() + "\n" + 
					"Data Tables: " + Integer.toString(size));
			if (size > 0) {
				log.fatal(this.provider.getTables());
			}
		}
	}
	
	/**
	 * @return
	 */
	public String getHtmlErrorMessage(TemplateFactory tf, ZipFactory zf, ConnectionFactory cf) {
		String message = "";
		Template errorTemplate;
		HashMap<String,String> parameters = new HashMap<>();
		parameters.put(Template.wrap("MESSAGE"),  this.error);
		parameters.put(Template.wrap("CONTEXT"),  this.context);
		parameters.put(Template.wrap("TRACE"), this.getStackTrace().toString());
		try {
			errorTemplate = tf.getTemplate("system.errHtml." + this.errorFromClass, "system.errHtml.", parameters);
			message = errorTemplate.merge(zf, tf, cf);
			errorTemplate.packageOutput(zf, cf);
		} catch (MergeException e) {
			message = "INVALID ERROR TEMPLATE! \n" +
					"Message: " + this.error + "\n" + 
					"Context: " + this.context + "\n";
		}
		return message;
	}

	/**
	 * @return
	 */
	public String getJsonErrorMessage(TemplateFactory tf, ZipFactory zf, ConnectionFactory cf) {
		String message = "";
		Template errorTemplate;
		HashMap<String,String> parameters = new HashMap<>();
		parameters.put(Template.wrap("MESSAGE"),  this.error);
		parameters.put(Template.wrap("CONTEXT"),  this.context);
		parameters.put(Template.wrap("TRACE"), this.getStackTrace().toString());
		try {
			errorTemplate = tf.getTemplate("system.errJson." + this.errorFromClass, "system.errJson.", parameters);
			message = errorTemplate.merge(zf, tf, cf);
			errorTemplate.packageOutput(zf, cf);
		} catch (MergeException e) {
			message = "INVALID ERROR TEMPLATE! \n" +
					"Message: " + this.error + "\n" + 
					"Context: " + this.context + "\n";
		}
		return message;
	}

	public String getMessage() {
		return super.getMessage() + " For: " + this.context;
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
