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

import org.apache.log4j.Logger;

/**
 * Simple custom Exception Class
 *
 * @author Mike Storey
 */
public class MergeException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger( MergeException.class.getName() );
	private String error;
	private String context;
	
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
		log.fatal("Merge Wrapped Exception: " + e.getClass().getName() + "\n" +
			"Message: " + this.error + "\n" +
			"Context: " + this.context + "\n" + 
			"StackTrace: ", this);
	}
	
	/**
	 * @param errorMessage
	 * @param theContext
	 */
	public MergeException(String errorMessage, String theContext){
		super(errorMessage);
		this.error = errorMessage;
		this.context = theContext;
		log.fatal("Merge Exception: \n" +
			"Message: " + this.error + "\n" +
			"Context: " + this.context + "\n" + 
			"StackTrace: ", this);
    }

	/**
	 * @return
	 */
	public String getHtmlErrorMessage() {
		return "<html><head></head><body>" +
		"<p>Message: " + this.error + "</p>" +
		"<p>Context: " + this.context + "</p>" + 
		"</body></html>";
	}

	public String getJsonErrorMessage() {
		// TODO Auto-generated method stub
		return "";
	}
}
