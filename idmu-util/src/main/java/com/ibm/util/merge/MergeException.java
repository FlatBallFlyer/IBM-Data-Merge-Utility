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
import java.util.Map;

import com.ibm.util.merge.directive.AbstractDirective;
import com.ibm.util.merge.directive.provider.AbstractProvider;
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
	private static final String KEY_ERROR 		= "MESSAGE";
	private static final String KEY_CONTEXT 	= "CONTEXT";
	private static final String KEY_TRACE 		= "TRACE";
	private static final String KEY_TEMPLATE 	= "TEMPLATE";
	private static final String KEY_CLASS 		= "CLASS";
	private static final String KEY_QUERY 		= "QUERY";
	private static final String KEY_SIZE 		= "SIZE";
	private final HashMap<String,String> state;
	
	/**
	 * Constructor
	 * 
	 * @param message The exception message
	 * @param errorCode The error code
	 */
	public MergeException(Exception e, String errorMessage, String theContext, Map<String,String> replace){
		super(e);
		state = new HashMap<String,String>();
		if (replace != null) {state.putAll(replace);}
		state.put(Template.wrap(KEY_ERROR), errorMessage);
		state.put(Template.wrap(KEY_CONTEXT), theContext);
		state.put(Template.wrap(KEY_TRACE), e.getStackTrace().toString());
		state.put(Template.wrap(KEY_CLASS), e.getClass().getName());
		state.put(Template.wrap(KEY_TEMPLATE), "");
		logError();
	}
	
	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(String errorMessage, String theContext, Map<String,String> replace){
		super(errorMessage);
		state = new HashMap<String,String>();
		if (replace != null) {state.putAll(replace);}
		state.put(Template.wrap(KEY_ERROR), errorMessage);
		state.put(Template.wrap(KEY_CONTEXT), theContext);
		state.put(Template.wrap(KEY_TRACE), this.getStackTrace().toString());
		state.put(Template.wrap(KEY_CLASS), "");
		state.put(Template.wrap(KEY_TEMPLATE), "");
		logError();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(Template errTemplate, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		state = new HashMap<String,String>();
		if (errTemplate != null) {state.putAll(errTemplate.getReplaceValues());}
		state.put(Template.wrap(KEY_ERROR), errorMessage);
		state.put(Template.wrap(KEY_CONTEXT), theContext);
		state.put(Template.wrap(KEY_TRACE), this.getStackTrace().toString());
		state.put(Template.wrap(KEY_CLASS), errTemplate.getClass().getName());
		state.put(Template.wrap(KEY_TEMPLATE), errTemplate.getFullName());
		logError();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(AbstractDirective errDirective, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		state = new HashMap<String,String>();
		if (errDirective != null) {state.putAll(errDirective.getTemplate().getReplaceValues());}
		state.put(Template.wrap(KEY_ERROR), errorMessage);
		state.put(Template.wrap(KEY_CONTEXT), theContext);
		state.put(Template.wrap(KEY_CLASS), errDirective.getClass().getName());
		state.put(Template.wrap(KEY_TRACE), this.getStackTrace().toString());
		state.put(Template.wrap(KEY_TEMPLATE), errDirective.getTemplate().getFullName());
		logError();
    }

	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(AbstractProvider errProvider, Exception wrapped, String errorMessage, String theContext){
		super(wrapped);
		state = new HashMap<String,String>();
		if (errProvider != null) {state.putAll(errProvider.getDirective().getTemplate().getReplaceValues());}
		state.put(Template.wrap(KEY_ERROR	), errorMessage);
		state.put(Template.wrap(KEY_CONTEXT	), theContext);
		state.put(Template.wrap(KEY_CLASS	), errProvider.getClass().getName());
		state.put(Template.wrap(KEY_TRACE	), this.getStackTrace().toString());
		state.put(Template.wrap(KEY_TEMPLATE), errProvider.getDirective().getTemplate().getFullName() );
		state.put(Template.wrap(KEY_QUERY	), errProvider.getQueryString());
		state.put(Template.wrap(KEY_SIZE	), Integer.toString(errProvider.size()));
		logError();
    }

	/**
	 * 
	 */
	private void logError() {
		String message = "Merge Exception Occured: \n";
		for (Map.Entry<String, String> entry : state.entrySet()) {
            message += "key:" + entry.getKey() + " value:" + entry.getValue() + "\n";
        }		
		log.fatal(message);
	}
	
	
	/**
	 * 
	 */

	public HashMap<String, String> getState() {
		return state;
	}

	public String getTemplateName() {
		return state.get(KEY_TEMPLATE);
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " For: " + state.get(KEY_CONTEXT);
	}
	
	public String getContext() {
		return state.get(KEY_CONTEXT);
	}

	public String getError() {
		return state.get(KEY_ERROR);
	}

	public String getErrorFromClass() {
		return state.get(KEY_CLASS);
	}
}
