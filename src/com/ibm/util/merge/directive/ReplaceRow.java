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

import org.apache.log4j.Logger;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.DataTable;

/**
 * @author Mike Storey
 *
 */
public abstract class ReplaceRow extends Directive implements Cloneable {
	private static final Logger log = Logger.getLogger( ReplaceRow.class.getName() );
	
	/**
	 * Database constructor
	 * @param dbRow
	 * @param owner
	 * @throws MergeException - Wrapped SQL Exceptions reading data
	 */
	public ReplaceRow(ResultSet dbRow, Template owner) throws MergeException {
		super(dbRow, owner);
	}

	/**
	 * clone constructor, deep-clone of notLast and onlyLast collections
	 * @see com.ibm.util.merge.directive.Directive#clone(com.ibm.util.merge.Template)
	 */
	public ReplaceRow clone(Template owner) throws CloneNotSupportedException {
		return (ReplaceRow) super.clone(owner);
	}

	/**
	 * @throws MergeException
	 */
	public void executeDirective() throws MergeException {
		this.provider.getData();

		// Make sure we got some data
		if ( this.provider.size() < 1 ) {
			throw new MergeException("No Data Found",this.provider.getQueryString()); 
		}

		// Make sure we don't have a multi-table result.
		if ( this.provider.size() > 1 ) {
			throw new MergeException("Multi-Talbe Empty Result set returned by Directive",this.provider.getQueryString()); 
		}
		DataTable table = this.provider.getTable(1);

		// Make sure we don't have an empty result set
		if ( table.size() == 0 ) {
			throw new MergeException("Empty Result set returned by Directive",this.provider.getQueryString()); 
		}

		// Make sure we don't have a multi-row result set
		if ( table.size() > 1 ) {
			throw new MergeException("Multiple rows returned when single row expected", this.provider.getQueryString());
		}
		
		// Add the replace values
		for (int col=1; col < table.cols(); col++) {
			this.template.addReplace(table.getName(col),table.getValue(1, col));
		}
		
		log.info("Values added by Replace Row:" + String.valueOf(table.cols()));
	}

}
