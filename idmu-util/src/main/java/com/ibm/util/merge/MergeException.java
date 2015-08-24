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

import com.ibm.util.merge.directive.AbstractDirective;
import com.ibm.util.merge.directive.provider.AbstractProvider;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.template.Template;
import org.apache.log4j.Logger;

/**
 * Merge Processing Exception Class - This is the only exception thrown by the Merge Utility
 *
 * @author Mike Storey
 */
public class MergeException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MergeException.class.getName() );
	private final String error;
	private final String context;
	private final Template template;
	private final AbstractDirective directive;
	private final AbstractProvider provider;
	private final String errorFromClass;
	
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
		errorFromClass = e.getClass().getName();
		template = null;
		provider = null;
		directive = null;
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
		errorFromClass = "";
		template = null;
		provider = null;
		directive = null;
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
		provider = null;
		directive = null;
		logError();
		logTemplate();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(AbstractDirective errDirective, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		error = errorMessage;
		context = theContext;
		directive = errDirective;
		template = directive.getTemplate();
		errorFromClass = errDirective.getClass().getName();
		provider = errDirective.getProvider();
		logError();
		logTemplate();
		logDirective();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(AbstractProvider errProvider, Exception wrapped, String errorMessage, String theContext){
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
				"Message: " + this.error + "\n" +
				"Context: " + this.context + "\n" +
				"FromClass: " + this.errorFromClass + "\n" +
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

	public String getTemplateName() {
		if (this.template == null) {
			return "";
		} else {
			return this.template.getFullName();
		}
	}
	@Override
	public String getMessage() {
		return super.getMessage() + " For: " + context;
	}
	
	public String getContext() {
		return context;
	}

	public String getError() {
		return error;
	}

	public String getErrorFromClass() {
		return errorFromClass;
	}
}
