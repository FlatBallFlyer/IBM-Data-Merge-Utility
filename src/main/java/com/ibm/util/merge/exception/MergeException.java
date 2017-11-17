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

import com.ibm.util.merge.Merger;

/**
 *  Custom Exception abstract base class - provides customized error messaging
 * @author flatballflyer
 *
 */
public abstract class MergeException extends Exception {

	private static final long serialVersionUID = 9055769776571167065L;
	private String error;
	
	public MergeException(String error) {
		super(error);
		this.error = error;
	}
	
	public String getErrorString() {
		return error;
	}
	
	public String getErrorMessage(Merger context) {
		// TODO Merge System Error Template.
		return "";
	}
	
}
