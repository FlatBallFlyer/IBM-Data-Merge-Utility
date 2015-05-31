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
package com.ibm.util.merge.directive;

import java.util.ArrayList;
import java.util.Arrays;

import com.ibm.util.merge.MergeException;

/**
 * A validation directive 
 * Allows a template to specify a set of replace values that must be on the 
 * replace stack. Supports more meaningful error messages on merge failures.
 * 
 * DEFERRED IMPLEMENTATION
 *
 * @author  Mike Storey
 */
public class Require extends Directive implements Cloneable {
	private ArrayList<String> tags = new ArrayList<String>();
	
	/**
	 * Simple Constructor
	 */
	public Require() {
		super();
		this.setType(TYPE_REQUIRE);
		this.setProvider(null);
	}

	/** 
	 * Simple clone constructor, deep copy the tags list
	 * @see com.ibm.util.merge.directive.Directive#clone(com.ibm.util.merge.Template)
	 */
	public Require clone() throws CloneNotSupportedException {
		return (Require) super.clone();
	}
	
	/**
	 * Check to see if the tags are in the replace stack, throw an exception if not found
	 * @see com.ibm.util.merge.directive.Directive#executeDirective()
	 */
	public void executeDirective() throws MergeException {
		for (String tag : this.tags) {
			if (! this.getTemplate().hasReplaceValue(tag) ) {
				throw new MergeException("Required Tag Not Found!", tag);
			}
		}
	}

	public String getTags() {
		return String.join(",", this.tags);
	}

	public void setTags(String tags) {
		this.tags = new ArrayList<String>(Arrays.asList(tags.split(",")));
	}


}
