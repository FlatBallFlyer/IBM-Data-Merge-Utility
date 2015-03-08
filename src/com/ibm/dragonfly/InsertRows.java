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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	 * <p>getValues will insert sub Templates into a parent template</p>
	 *
	 * @param  target Template to insert sub-templates into
	 * @throws DragonFlyException  Infinite Loop Safety, or subTemplate Create/Merge 
	 * @throws DragonFlySqlException SQL Error thrown in Template.new
	 */
	public void getValues(Template target) throws DragonFlyException, DragonFlySqlException {
		log.info("Inserting Subtemplates into: " + target.getFullName());
		
		// Depth counter - infinite loop safety mechinism
		if (!target.getReplaceValues().containsKey(Template.TAG_STACK)) {
			throw new DragonFlyException("Template Insert Stack Tag Not Found!",Template.TAG_STACK);
		} else if (target.getReplaceValues().get(Template.TAG_STACK).split("/").length >= DEPTH_MAX) {
			throw new DragonFlyException("Sub-Template Insert Depth exceeded! Infinite Loop Saftey triggered", 
					target.getReplaceValues().get(Template.TAG_STACK) );
		}

		// Select the result set, and insert sub-templates at bookmarks
		try {
			ResultSet rs = this.getResultSet(target.getReplaceValues());
			String colName = "";
			int count = 0;
			// Iterate over the result set 
			while (rs.next()) {
				count ++;
				log.info("Inserting Record #" + count + " into: " + target.getFullName());
				// Iterate over target bookmarks
	 			for(Bookmark bookmark: target.getBookmarks()) {
					try {
						colName = (this.columnName.isEmpty()) ? "" : rs.getString(this.columnName);
					} catch (SQLException e) {
						throw new DragonFlySqlException("Insert Rows Error", "Column " + this.columnName + " was not found", 
								this.getQueryString(target.getReplaceValues()), 
								e.getMessage());
					}

	 				// Create the new sub-template
					Template subTemplate = TemplateFactory.getTemplate(this.collection, colName, bookmark.getName(),target.getReplaceValues());
					
					// Add the row-level replace values
					subTemplate.addRowReplace(rs);
					
					// Take care of "Not Last" and "Only Last"
					subTemplate.addEmptyReplace(rs.isLast() ? this.notLast : this.onlyLast);
					
					// Merge the SubTemplate and insert the text into the Parent Template
					log.info("Inserting Template " + subTemplate.getFullName() + " into " + target.getFullName());
					target.insertText(subTemplate.merge(), bookmark);
	 			}		
			}
			this.close();
		} catch (SQLException e) {
			throw new DragonFlySqlException("Insert Rows Error", "Error Iterating Resultset", 
					this.getQueryString(target.getReplaceValues()), 
					e.getMessage());			
		} catch (IOException e) {
			throw new DragonFlyException( "SubTemplate Save IO Exception", e.getMessage());			
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
