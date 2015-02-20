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
	private String description;
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
			this.description	= dbRow.getString("description");
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
	
	/**
	 * <p>Get Description</p>
	 *
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}
}
