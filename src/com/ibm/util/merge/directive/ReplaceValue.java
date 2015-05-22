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
import org.apache.log4j.Logger;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;

/**
 * A simple replace From with To directive 
 *
 * @author  Mike Storey
 */
public class ReplaceValue extends Directive implements Cloneable {
	private static final Logger log = Logger.getLogger( ReplaceValue.class.getName() );
	private String from = "";
	private String to = "";

	/**
	 * Database Constructor
	 * @param dbRow the Result Set row containt the Directive data
	 * @param owner the Template that "owns" this directive.
	 */
	public ReplaceValue(ResultSet dbRow, Template owner) throws MergeException {
		super(dbRow, owner);
		try {
			this.from = dbRow.getString(Directive.COL_REPLACE_FROM_VALUE);
			this.to = dbRow.getString(Directive.COL_REPLACE_TO_VALUE);
		} catch (SQLException e) {
			throw new MergeException(e, "Insert Subs Constructor Error", this.getFullName());
		}
	}

	/**
	 * Simple clone
	 */
	public Directive clone(Template owner) throws CloneNotSupportedException {
		return super.clone(owner);
	}

	/**
	 * Add the replace value
	 */
	public void executeDirective() {
		this.template.addReplace(this.from, this.to);
		log.info("Replaced " + this.from + " with " + this.to);
	}


}
