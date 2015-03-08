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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	protected String description;
	private String jndiSource;
	private String selectColumns;
	private String fromTables;
	private String whereCondition;

	private Connection con;
	private Statement st;
	private ResultSet rs;
		
	/**
	 * <p>Constructor</p>
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
			throw new DragonFlyException("Replace Column Error: "+e.getMessage(), "Invalid Directive Data");
		}
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
	 * Create and execute the query, returing the results set. 
	 *  NOTE: Close should be called when you are through with the result set!
	 *  
	 * @param replaceValues From/To hash used on the SQL Select Statment
	 * @throws DragonFlyException JNDI Data Source Connection Errors
	 * @throws DragonFlySqlException  SQL Database Errors
	 * @return ResultSet The result of the SQL Query
	 */
	public ResultSet getResultSet(Map<String,String> replaceValues ) throws DragonFlySqlException, DragonFlyException {
		String queryString = this.getQueryString(replaceValues);

		try {
			this.con = ConnectionFactory.getDataConnection(this.jndiSource);
			this.st = con.createStatement();
			this.rs = st.executeQuery(queryString);
			return this.rs;
		} catch (SQLException e) {
			throw new DragonFlySqlException("Insert Rows Error", "Connection Failure", queryString, e.getMessage());
		}
	}
	
	/**
	 * Close the database connection 
	 *  
	 * @throws DragonFlySqlException  SQL Database Errors
	 */
	public void close() throws DragonFlySqlException {
		try {
			this.rs.close();
			this.st.close();
			this.con.close();
		} catch (SQLException e) {
			throw new DragonFlySqlException("SQL Query - Housekeeping Error!", "Connection Failure", "Close Connection", e.getMessage());
		}
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