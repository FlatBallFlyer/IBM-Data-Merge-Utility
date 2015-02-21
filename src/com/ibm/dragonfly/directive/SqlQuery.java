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
package com.ibm.dragonfly.directive;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.ibm.dragonfly.ConnectionFactory;
import com.ibm.dragonfly.tkException;
import com.ibm.dragonfly.tkSqlException;

/**
 * A simple wrapper for the Template Select Query builder 
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     InsertRows
 * @see 	ReplaceRow
 * @see 	ReplaceCol
 */
public class SqlQuery {
	private String jndiSource;
	private String selectColumns;
	private String fromTables;
	private String whereCondition;

	private Connection con;
	private Statement st;
	private ResultSet rs;

	/**
	 * Simple constructor 
	 *
	 */
	public SqlQuery(String source, String columns, String table, String where ) {
		this.jndiSource = source;
		this.selectColumns = columns;
		this.fromTables = table;
		this.whereCondition = where;
	}

	/**
	 * Get the select statement 
	 *  
	 */
	public String getQueryString(Map<String,String> replaceValues) {
		String queryString = "SELECT " + this.selectColumns + " FROM " + this.fromTables;
		if ( !this.whereCondition.isEmpty() ) {
			// run replace stack over where condition
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
	 * @throws tkException - JNDI Data Source Connection Errors
	 * @throws tkSqlException  - SQL Database Errors
	 */
	public ResultSet getResultSet(Map<String,String> replaceValues ) throws tkSqlException, tkException {
		String queryString = this.getQueryString(replaceValues);

		try {
			this.con = ConnectionFactory.getDataConnection(this.jndiSource);
			this.st = con.createStatement();
			this.rs = st.executeQuery(queryString);
			return this.rs;
		} catch (SQLException e) {
			throw new tkSqlException("Insert Rows Error", "Connection Failure", queryString, e.getMessage());
		}
	}
	
	/**
	 * Close the database connection 
	 *  
	 * @throws tkSqlException  - SQL Database Errors
	 */
	public void close() throws tkSqlException {
		try {
			this.rs.close();
			this.st.close();
			this.con.close();
		} catch (SQLException e) {
			throw new tkSqlException("SQL Query - Housekeeping Error!", "Connection Failure", "Close Connection", e.getMessage());
		}
	}
	
	
	
}
