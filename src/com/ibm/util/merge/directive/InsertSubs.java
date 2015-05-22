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
package com.ibm.util.merge.directive;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.util.merge.Bookmark;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.directive.provider.DataTable;

/**
 * The Insert Subs directive drives the insertion of sub-templates at bookmarks 
 * for each row of data in the DataTables provided by the Data Provider
 * @author flatballflyer
 *
 */
public abstract class InsertSubs extends Directive implements Cloneable{
	private static final Logger log = Logger.getLogger( InsertSubs.class.getName() );
	private static final int DEPTH_MAX = 100;
	private String collectionName 	= "";
	private String collectionColumn = "";
	private List<String> notLast	= new ArrayList<String>();	// Not-Last Tags for Insert Directives
	private List<String> onlyLast	= new ArrayList<String>();	// Only-Last Tags for Insert Directives

	/**
	 * Database constructor
	 * @param dbRow
	 * @param owner
	 * @throws MergeException - Wrapped SQL Exceptions reading data
	 */
	public InsertSubs(ResultSet dbRow, Template owner) throws MergeException {
		super(dbRow, owner);
		try {
			this.collectionName = dbRow.getString(Directive.COL_INSERT_FROM_COLLECTION);
			this.collectionColumn = dbRow.getString(Directive.COL_INSERT_FROM_COLUMN);
			this.notLast = new ArrayList<String>(Arrays.asList(dbRow.getString(Directive.COL_INSERT_NOT_LAST).split(",")));
			this.onlyLast = new ArrayList<String>(Arrays.asList(dbRow.getString(Directive.COL_INSERT_ONLY_LAST).split(",")));
		} catch (SQLException e) {
			throw new MergeException(e, "Insert Subs Constructor Error", this.getFullName());
		}
	}

	/**
	 * clone constructor, deep-clone of notLast and onlyLast collections
	 * @see com.ibm.util.merge.directive.Directive#clone(com.ibm.util.merge.Template)
	 */
	public InsertSubs clone(Template owner) throws CloneNotSupportedException {
		InsertSubs newDirective = (InsertSubs) super.clone(owner);
		newDirective.notLast	= new ArrayList<String>(this.notLast);
		newDirective.onlyLast	= new ArrayList<String>(this.onlyLast);
		return newDirective;
	}
	
	/**
	 * Execute Directive - will get the data and insert the sub-templates
	 *
	 * @param  target Template to insert sub-templates into
	 * @throws MergeException  Infinite Loop Safety, or subTemplate Create/Merge 
	 * @throws DragonFlySqlException SQL Error thrown in Template.new
	 */
	public void executeDirective() throws MergeException {
		log.info("Inserting Subtemplates into: " + this.template.getFullName());
		
		// Depth counter - infinite loop safety mechanism
		if (this.template.getStack().split("/").length >= DEPTH_MAX) {
			throw new MergeException("Insert Subs Infinite Loop suspected", this.getFullName()); 
		}

		// Get the table data and iterate the rows
		this.provider.getData();
		for (DataTable table : this.provider.getTables() ) {
				
			for( int row = 1; row < table.size(); row++ ) {
				log.info("Inserting Record #" + row + " into: " + this.template.getFullName());
	
				// Iterate over target bookmarks
		 		for(Bookmark bookmark : this.template.getBookmarks()) {
	
	 				// Create the new sub-template
		 			String colValue = table.getValue(row, this.collectionColumn);
					Template subTemplate = TemplateFactory.getTemplate(this.collectionName, colValue, bookmark.getName(), this.template.getReplaceValues());
					log.info("Inserting Template " + subTemplate.getFullName() + " into " + this.template.getFullName());
						
					// Add the Row replace values
					for (int col=1; col < table.cols(); col++) {
						this.template.addReplace(table.getName(col), table.getValue(1, col));
					}
						
					// Take care of "Not Last" and "Only Last"
					subTemplate.addEmptyReplace(row == table.size() ? this.notLast : this.onlyLast);
						
					// Merge the SubTemplate and insert the text into the Target Template
					try {
						this.template.insertText(subTemplate.merge(), bookmark);
					} catch (MergeException e) {
						if ( this.softFail()) {
							log.warn("Soft Fail on Insert");
							this.template.insertText("Soft Fail Exception" + e.getMessage(), bookmark);
						} else {
							throw e;
						}
					}
				}
			}
		}
	}
}
