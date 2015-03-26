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
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

import org.apache.log4j.Logger;


/**
 * <p>This class represents a insertRows directive which inserts sub-templates
 * at bookmarks for each row of the SQL result set.</p>
 *
 * @author  Mike Storey
 */
class InsertRows extends SqlDirective {
	// Insert Directive Constants
	private static final int DEPTH_MAX = 50;
	private static final Logger log = Logger.getLogger( InsertRows.class.getName() );
	
	// Instance Variables
	private String collection;
	private String columnName;
	private ArrayList<String> notLast = new ArrayList<String>();
	private ArrayList<String> onlyLast = new ArrayList<String>();

	/**
	 * <p>Constructor</p>
	 *
	 * @param  dbRow Database Result Set Row Hash 
	 * @throws DragonFlySqlException Merge Data errors
	 * @throws DragonFlyException  Invalid Directive Data
	 */
	public InsertRows(ResultSet dbRow) throws DragonFlySqlException, DragonFlyException  {
		super(dbRow);
		try {
			this.collection		= dbRow.getString("collection");
			this.columnName		= dbRow.getString("columnName");
			this.notLast.addAll(Arrays.asList(dbRow.getString("notLast").split(",")));
			this.onlyLast.addAll(Arrays.asList(dbRow.getString("onlyLast").split(",")));
		} catch (SQLException e) {
			throw new DragonFlyException("Insert Rows Error: " + e.getMessage(), "Invalid Directive Data");
		}
	}

	/**
	 * <p>Clone Constructor</p>
	 *
	 * @param  from object to clone 
	 */
	public InsertRows(InsertRows from) {
		super(from);
		this.collection = from.collection;
		this.columnName = from.columnName;
		this.notLast 	= new ArrayList<String>();
		this.onlyLast	= new ArrayList<String>();
		this.notLast.addAll(from.notLast);
		this.onlyLast.addAll(from.onlyLast);
	}

	/**
	 * <p>getValues will insert sub Templates into a parent template</p>
	 *
	 * @param  target Template to insert sub-templates into
	 * @throws DragonFlyException  Infinite Loop Safety, or subTemplate Create/Merge 
	 * @throws DragonFlySqlException SQL Error thrown in Template.new
	 */
	public void getValues(Template target) throws DragonFlyException, DragonFlySqlException {
		log.info("Inserting Subtemplates into: " + target.getFullName());
		int count = 0;
		Connection con = null;
		
		// Depth counter - infinite loop safety mechinism
		if (!target.getReplaceValues().containsKey(Template.TAG_STACK)) {
			throw new DragonFlyException("Template Insert Stack Tag Not Found!",Template.TAG_STACK);
		} else if (target.getReplaceValues().get(Template.TAG_STACK).split("/").length >= DEPTH_MAX) {
			String message = "Sub-Template Insert Depth exceeded! Infinite Loop Saftey triggered" + target.getReplaceValues().get(Template.TAG_STACK);
			log.fatal(message);
			throw new DragonFlyException(message, "Stack Safety"); 
		}

		// Select the result set, and insert sub-templates at bookmarks
		try {
			String queryString = this.getQueryString(target.getReplaceValues());
			con = ConnectionFactory.getDataConnection(this.jndiSource, target.getOutputFile());
			PreparedStatement st = con.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery();
			String colName = "";
			// Iterate over the result set 
			while (rs.next()) {
				count ++;
				log.info("Inserting Record #" + count + " into: " + target.getFullName());
				// Iterate over target bookmarks
	 			for(Bookmark bookmark : target.getBookmarks()) {
					try {
						colName = (this.columnName.isEmpty()) ? "" : rs.getString(this.columnName);
					} catch (SQLException e) {
						throw new DragonFlySqlException("Insert Rows Error", "Column " + this.columnName + " was not found", 
								queryString, 
								e.getMessage());							
					}

	 				// Create the new sub-template
					Template subTemplate = TemplateFactory.getTemplate(this.collection, colName, bookmark.getName(), target.getReplaceValues());
					
					// Add the row-level replace values
					subTemplate.addRowReplace(rs);
					
					// Take care of "Not Last" and "Only Last"
					subTemplate.addEmptyReplace(rs.isLast() ? this.notLast : this.onlyLast);
					
					// Merge the SubTemplate and insert the text into the Target Template
					try {
						log.info("Inserting Template " + subTemplate.getFullName() + " into " + target.getFullName());
						target.insertText(subTemplate.merge(), bookmark);
					} catch (DragonFlyException e) {
						if ( target.getReplaceValues().containsKey(Template.TAG_SOFTFAIL) ) {
							log.warn("Soft Fail on Insert");
							target.insertText("Soft Fail Exception" + e.getMessage(), bookmark);
						} else {
							throw e;
						}
					}
	 			}		
			}
		} catch (SQLException e) {
			throw new DragonFlySqlException("Insert Rows Error", "Error Iterating Resultset", 
					this.getQueryString(target.getReplaceValues()), 
					e.getMessage());			
		} catch (IOException e) {
			throw new DragonFlyException( "SubTemplate Save IO Exception", e.getMessage());			
		} finally {
			log.info("Inserted " + String.valueOf(count) + " Sub Templates into " + target.getFullName());
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
