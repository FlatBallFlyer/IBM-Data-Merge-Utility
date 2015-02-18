/*
 * Copyright 2015 IBM
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
package com.ibm.tk.directive;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.tk.tkException;

/**
 * A simple replace From -> To directive 
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Merge
 */
public class ReplaceValue {
	private static final Logger log = Logger.getLogger( ReplaceValue.class.getName() );
	private String description;
	private String fromValue;
	private String toValue;

	/**
	 * <p>Constructor</p>
	 *
	 * @param  From Value
	 * @param  To Value
	 * @return The new Directive object
	 * @throws SQLException - Bad dbRow, or missing fromValue/toValue in result
	 */
	public ReplaceValue (ResultSet dbRow) throws tkException {
		log.finest("Constructing ReplaceValue Directive");
		try {
			this.description	= dbRow.getString("description");
			this.fromValue 	= dbRow.getString("fromValue");
			this.toValue 	= dbRow.getString("toValue");
		} catch (SQLException e) {
			throw new tkException("Replace Value Error: "+e.getMessage(), "Invalid Directive Data");
		}
	}
	
	/**
	 * <p>Get Hashtable From/To</p>
	 *
	 * @return Hashtable with From->To
	 */
	public void getValues(Map<String,String> replace) {
		log.finest("Adding " + this.fromValue + "->" + this.toValue);
		replace.put(this.fromValue, this.toValue);
	}

	/**
	 * <p>Get Description</p>
	 *
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}
}
