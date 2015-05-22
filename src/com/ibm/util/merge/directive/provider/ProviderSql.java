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
package com.ibm.util.merge.directive.provider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.ibm.util.merge.ConnectionFactory;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.Directive;

/**
 * @author flatballflyer
 *
 */
public class ProviderSql extends Provider implements Cloneable {
	private static final Logger log = Logger.getLogger( ProviderSql.class.getName() );
	private String source;
	private String columns;
	private String tables;
	private String where;

	/**
	 * Database constructor
	 * @param newOwner - the Template that owns this directive
	 * @param dbRow - The Sql ResultSet row containing Directive data
	 * @throws MergeException - Wrapped SQL exceptions.
	 */
	public ProviderSql(Directive newOwner, ResultSet dbRow) throws MergeException {
		super(newOwner);
		try {
			this.source 	= dbRow.getString(Directive.COL_JDBC_SOURCE);
			this.columns 	= dbRow.getString(Directive.COL_JDBC_COLUMNS);
			this.tables 	= dbRow.getString(Directive.COL_JDBC_TABLES);
			this.where 		= dbRow.getString(Directive.COL_JDBC_WHERE);
		} catch (SQLException e) {
			throw new MergeException(e, "ProviderSql Construction SQL Error", this.getQueryString());
		}
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderSql clone(Directive newOwner) throws CloneNotSupportedException {
		return (ProviderSql) super.clone();
	}

	/**
	 * Prepare and execute a SQL statement, then load the result set into a single DataTable
	 * 
	 * @throws MergeException Wrapped SQL and Process execptions
	 */
	@Override
	public void getData() throws MergeException {
		DataTable table = this.reset();
		Connection con = null;
		
		try {
			// Prepare and Execute the SQL Statement
			String queryString = this.getQueryString();
			con = ConnectionFactory.getDataConnection(this.source, this.directive.getTemplate().getOutputFile());
			PreparedStatement st = con.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery();
			
			// Populate the Table Column names
			ResultSetMetaData meta = rs.getMetaData();
			final int columnCount = meta.getColumnCount();
		    for (int column = 1; column <= columnCount; column++) 
		    {
		    	table.addCol(meta.getColumnName(column));
		    }
			
			// Populate the Table Data
			while (rs.next() ) {
				ArrayList<String> row = table.getNewRow();
			    for (int column = 1; column <= columnCount; column++) 
			    {
			    	String value = rs.getString(column);
			    	row.add( value != null ? value : "");
			    }
			}
		} catch (SQLException e) {
			throw new MergeException(e, "Invalid Merge Data", this.getQueryString() );
		} finally {
			log.info("Sql Dataprovider read " + Integer.toString(table.size()) + " rows");			
		}
	}

	/**
	 * Get the select statement 
	 *  
	 * @param  replaceValues Replace Hash to use
	 * @return the Select Statement
	 */
	public String getQueryString() {
		String query = "SELECT " + this.directive.getTemplate().replaceProcess(this.columns);
		if ( !this.tables.isEmpty()) {
			query += " FROM " + this.directive.getTemplate().replaceProcess(this.tables);
		}
		if ( !this.where.isEmpty() ) {
			query += " WHERE " + this.directive.getTemplate().replaceProcess(this.where);
		}
		return query;
	}
}
