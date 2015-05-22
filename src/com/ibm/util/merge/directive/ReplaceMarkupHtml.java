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

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;

public class ReplaceMarkupHtml extends Directive implements Cloneable {
	protected String pattern;
	protected String fromKey;
	protected String toKey;

	/**
	 * Database Constructor
	 * @param dbRow the Result Set row containt the Directive data
	 * @param owner the Template that "owns" this directive.
	 */
	public ReplaceMarkupHtml(ResultSet dbRow, Template owner) throws MergeException {
		super(dbRow, owner);
		try {
			this.pattern = dbRow.getString(Directive.COL_HTTP_URL);
			this.fromKey = dbRow.getString(Directive.COL_HTTP_URL);
			this.toKey = dbRow.getString(Directive.COL_HTTP_URL);
		} catch (SQLException e) {
			throw new MergeException(e, "Replace Markup Constructor Error", this.getFullName());
		}
	}

	/**
	 * Simple clone
	 */
	public ReplaceMarkupHtml clone(Template owner) throws CloneNotSupportedException {
		return (ReplaceMarkupHtml) super.clone(owner);
	}

	/**
	 * Add the replace value
	 * @throws MergeException 
	 */
	public void executeDirective() throws MergeException {
		this.provider.getData();
		// parse this.provider.retrievedData
		// for each match, get from/to and call this.owner.addReplace()
	}
}
