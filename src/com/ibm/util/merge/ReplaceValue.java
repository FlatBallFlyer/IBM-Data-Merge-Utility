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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * A simple replace From with To directive 
 *
 * @author  Mike Storey
 */
class ReplaceValue {
	private static final Logger log = Logger.getLogger( ReplaceValue.class.getName() );
	private String description;
	private String fromValue;
	private String toValue;

	/**
	 * <p>Constructor from Database Row</p>
	 *
	 * @param  dbRow Directive database row
	 * @throws DragonFlyException Bad dbRow bad Template Data
	 */
	public ReplaceValue (ResultSet dbRow) throws DragonFlyException {
		try {
			this.description	= dbRow.getString("description");
			this.fromValue 	= dbRow.getString("fromValue");
			this.toValue 	= dbRow.getString("toValue");
		} catch (SQLException e) {
			throw new DragonFlyException("Replace Value Error: "+e.getMessage(), "Invalid Directive Data");
		}
	}
	
	/**
	 * <p>Clone</p>
	 *
	 * @param  from Replace Value object to clone
	 */
	public ReplaceValue (ReplaceValue from) {
		this.description	= from.description;	
		this.fromValue 		= from.fromValue;
		this.toValue 		= from.toValue;
	}

	/**
	 * <p>Get Hashtable From/To</p>
	 *
	 * @param  target The template to place values in
	 */
	public void getValues(Template target) {
		target.getReplaceValues().put(this.fromValue, this.toValue);
		log.debug("Replace Value " + this.fromValue + " -> " + this.toValue);
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
