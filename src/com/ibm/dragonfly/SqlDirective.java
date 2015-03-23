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
package com.ibm.dragonfly;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * <p>Abstract base class for all replace directives that are SQL based.
 * This class implements the SQL processing used by the Insert and Replace SQL directives</p>
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Merge
 */

abstract class SqlDirective {
	private static final Logger log = Logger.getLogger( SqlDirective.class.getName() );

	protected String description;
	protected String jndiSource;
	protected String selectColumns;
	protected String fromTables;
	protected String whereCondition;

	/**
	 * <p>Database Row Constructor</p>
	 *
	 * @param  dbRow Database Result Set Single Row Hash of Directive Table
	 * @throws DragonFlyException Invalid Row or Missing Column in Directive SQL Construction
	 */
	public SqlDirective(ResultSet dbRow) throws DragonFlyException  {
		try {
			this.description	= dbRow.getString("description");
			this.jndiSource = dbRow.getString("jndiSource");
			this.selectColumns = dbRow.getString("selectColumns");
			this.fromTables = dbRow.getString("fromTables");
			this.whereCondition = dbRow.getString("whereCondition");
		} catch (SQLException e) {
			log.fatal("Directive Construction Error, invalid Tempalte Directive table? " + e.getMessage() );
			throw new DragonFlyException("Replace Column Error: "+e.getMessage(), "Invalid Directive Data");
		}
	}

	/**
	 * <p>Clone Constructor</p>
	 *
	 * @param  from Object to clone
	 */
	public SqlDirective(SqlDirective from) {
		this.description = from.description;
		this.jndiSource = from.jndiSource;
		this.selectColumns = from.selectColumns;
		this.fromTables = from.fromTables;
		this.whereCondition = from.whereCondition;
	}
	
	/**
	 * <p>Get Values must be implemented by sub-class and is the execut the directive method</p>
	 * 
	 * @param  target Target Template Object
	 * @throws DragonFlyException Empty Result Set Data Source Error
	 * @throws DragonFlySqlException Database Connection Error
	 */
	public abstract void getValues(Template target) throws DragonFlyException, DragonFlySqlException;

	/**
	 * Get the select statement 
	 *  
	 * @param  replaceValues Replace Hash to use
	 * @return the Select Statement
	 */
	public String getQueryString(Map<String,String> replaceValues) {
		String columns = this.selectColumns;
		for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
			columns = columns.replace(entry.getKey(), entry.getValue());
		}
		String queryString = "SELECT " + columns;
		
		if ( !this.fromTables.isEmpty()) {
			String tables = this.fromTables;
			for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
				tables = tables.replace(entry.getKey(), entry.getValue());
			}
			queryString += " FROM " + tables;
		}
		
		if ( !this.whereCondition.isEmpty() ) {
			String where = this.whereCondition;
			for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
			  where = where.replace(entry.getKey(), entry.getValue());
			}
			queryString += " WHERE " + where;
		}
		return queryString;
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