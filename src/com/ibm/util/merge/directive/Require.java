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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;

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
	private ArrayList<String> tags;
	
	/**
	 * @param dbRow
	 * @param newOwner
	 * @throws MergeException
	 */
	public Require(ResultSet dbRow, Template newOwner) throws MergeException {
		super(dbRow, newOwner);
		try {
			this.tags = new ArrayList<String>(Arrays.asList(dbRow.getString(Directive.COL_REQUIRE_TAGS).split(",")));
		} catch (SQLException e) {
			throw new MergeException(e, "Require Tags Constructor Error", this.getFullName());
		}
	}

	/** 
	 * Simple clone constructor, deep copy the tags list
	 * @see com.ibm.util.merge.directive.Directive#clone(com.ibm.util.merge.Template)
	 */
	public Require clone(Template owner) throws CloneNotSupportedException {
		Require newDirective = (Require) super.clone(owner);
		newDirective.tags	= new ArrayList<String>(this.tags);
		return newDirective;
	}
	
	/**
	 * Check to see if the tags are in the replace stack, throw an exception if not found
	 * @see com.ibm.util.merge.directive.Directive#executeDirective()
	 */
	public void executeDirective() throws MergeException {
		for (String tag : this.tags) {
			if (! this.template.hasReplaceValue(tag) ) {
				throw new MergeException("Required Tag Not Found!", tag);
			}
		}
	}
}
