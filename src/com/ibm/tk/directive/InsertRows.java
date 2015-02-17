/*
 * Copyright (c) 2015 IBM 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package com.ibm.tk.directive;
import com.ibm.tk.ConnectionFactory;
import com.ibm.tk.TemplateFactory;
import com.ibm.tk.tkException;
import com.ibm.tk.SqlQuery;
import com.ibm.tk.Bookmark;
import com.ibm.tk.Template;
import com.ibm.tk.tkSqlException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p>This class represents a insertRows directive which inserts sub-templates
 * at bookmarks for each row of the SQL result set.</p>
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Directive
 * @see     Merge
 */
public class InsertRows {
	private static final Logger log = Logger.getLogger( InsertRows.class.getName() );
	private String collection;
	private String columnName;
	private SqlQuery theQuery;
	private ArrayList<String> notLast = new ArrayList<String>();
	private ArrayList<String> onlyLast = new ArrayList<String>();

	/**
	 * <p>Constructor</p>
	 *
	 * @param  Database Result Set Row Hash 
	 * @throws Exception - 
	 * @return The new insertRows object
	 * @throws tkException  - Invalid Directive Data
	 */
	public InsertRows(ResultSet dbRow) throws tkSqlException, tkException  {
		try {
			this.collection		= dbRow.getString("collection");
			this.columnName		= dbRow.getString("columnName");
			this.theQuery		= new SqlQuery(	dbRow.getString("selectColumns"),
												dbRow.getString("fromTables"),
												dbRow.getString("whereCondition"));
			
			this.notLast.addAll(Arrays.asList(dbRow.getString("notLast").split(",")));
			this.onlyLast.addAll(Arrays.asList(dbRow.getString("onlyLast").split(",")));
		} catch (SQLException e) {
			throw new tkException("Insert Rows Error: " + e.getMessage(), "Invalid Directive Data");
		}
	}

	/**
	 * <p>insertTemplates - This method drives the insertion of sub-templates
	 * into a parent template</p>
	 *
	 * @param  Template to insert sub-templates into
	 * @throws SQLException - Data Source Connection and Execution errors
	 * @throws tkException -  on subTemplate Create or Merge
	 * @throws IOException - Subtemplate Save Output Errors
	 * @throws tkSqlException - SQL Error thrown in Template->new
	 */
	public void insertTemplates(Template target) throws tkException, IOException, tkSqlException {
		log.fine("Inserting Subtemplates into: " + target.getFullName());
		Connection con;
		Statement st;
		ResultSet rs;
		String queryString = theQuery.queryString(target.getReplaceValues());
		try {
			// Get a connection and execute the query
			con = ConnectionFactory.getDataConnection();
			st = con.createStatement();
			rs = st.executeQuery(queryString);
		} catch (SQLException e) {
			throw new tkSqlException("Insert Rows Error", "Connection Failure", queryString, e.getMessage());
		}
		
		try {
			int count = 0;
			// Iterate over the result set 
			while (rs.next()) {
				count ++;
				log.finer("Inserting Record #" + count + " into: " + target.getFullName());
				// Iterate over target bookmarks
	 			for(Bookmark bookmark: target.getBookmarks()) {
	 				String colName;
					try {
						colName = (this.columnName.isEmpty()) ? "" : rs.getString(this.columnName);
					} catch (SQLException e) {
						throw new tkSqlException("Insert Rows Error", "Column " + this.columnName + " was not found", queryString, e.getMessage());
					}

	 				// Create the new sub-template
					Template subTemplate = TemplateFactory.getTemplate(this.collection, colName, bookmark.getName());
					
					// Clone the parent templates replace values into the subTemplate
					subTemplate.getReplaceValues().putAll(target.getReplaceValues());
					
					// Add the row-level replace values
					subTemplate.addRowReplace(rs);
					
					// Take care of "Not Last" and "Only Last"
					subTemplate.addEmptyReplace(rs.isLast() ? this.notLast : this.onlyLast);
					
					// Merge the SubTemplate and insert the text into the Parent Template
					log.finest("Inserting Template " + subTemplate.getFullName() + " into " + target.getFullName());
					target.insertText(subTemplate.merge(), bookmark);
	 			}		
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			throw new tkSqlException("Insert Rows Error", "Error Iterating Resultset", queryString, e.getMessage());			
		}
	}	
}
