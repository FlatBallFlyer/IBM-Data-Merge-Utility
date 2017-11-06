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

/**
 * Processing Error exception
 * 
 * @author Mike Storey
 *
 */
public class Merge500 extends MergeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6431760991496776020L;

	public Merge500(String error) {
		super(error);
	}

}
