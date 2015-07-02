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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.util.merge.*;
import org.apache.log4j.Logger;
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
	
	private List<String> notLast  = new ArrayList<String>();	
	private List<String> onlyLast = new ArrayList<String>();	

	/**
	 * Simple constructor
	 */
	public InsertSubs() {
		super();
	}

	/**
	 * clone constructor, deep-clone of notLast and onlyLast collections
	 * @see com.ibm.util.merge.directive.Directive#clone(com.ibm.util.merge.Template)
	 */
	public InsertSubs clone() throws CloneNotSupportedException {
		InsertSubs newDirective = (InsertSubs) super.clone();
		newDirective.notLast	= new ArrayList<String>(this.notLast);
		newDirective.onlyLast	= new ArrayList<String>(this.onlyLast);
		return newDirective;
	}
	
	/**
	 * Execute Directive - will get the data and insert the sub-templates
	 *
	 * @param  target Template to insert sub-templates into
	 * @param tf
	 * @param cf
	 * @param zf
	 * @throws MergeException  Infinite Loop Safety, or subTemplate Create/Merge
	 * @throws DragonFlySqlException SQL Error thrown in Template.new
	 */
	public void executeDirective(TemplateFactory tf, ConnectionFactory cf, ZipFactory zf) throws MergeException {
		log.info("Inserting Subtemplates into: " + this.getTemplate().getFullName());
		
		// Depth counter - infinite loop safety mechanism
		if (this.getTemplate().getStack().split("/").length >= DEPTH_MAX) {
			throw new MergeException("Insert Subs Infinite Loop suspected", this.getFullName()); 
		}

		// Get the table data and iterate the rows
		this.getProvider().getData(cf);
		for (DataTable table : this.getProvider().getTables() ) {
				
			for( int row = 0; row < table.size(); row++ ) {
				log.info("Inserting Record #" + row + " into: " + this.getTemplate().getFullName());
	
				// Iterate over target bookmarks
		 		for(Bookmark bookmark : this.getTemplate().getBookmarks()) {
	
	 				// Create the new sub-template
		 			String collection = bookmark.getCollection();
		 			String name = bookmark.getName();
		 			String column = table.getValue(row, bookmark.getColumn());
					Template subTemplate = tf.getTemplate(collection + "." + name + "." + column, collection + "." + name + ".", this.getTemplate().getReplaceValues());
					log.info("Inserting Template " + subTemplate.getFullName() + " into " + this.getTemplate().getFullName());
						
					// Add the Row replace values
					for (int col=0; col < table.cols(); col++) {
						subTemplate.addReplace(table.getCol(col), table.getValue(row, col));
					}
						
					// Take care of "Not Last" and "Only Last"
					subTemplate.addEmptyReplace(row == table.size()-1 ? this.notLast : this.onlyLast);
						
					// Merge the SubTemplate and insert the text into the Target Template
					try {
						this.getTemplate().insertText(subTemplate.merge(zf, tf, cf), bookmark);
					} catch (MergeException e) {
						if ( this.softFail()) {
							log.warn("Soft Fail on Insert");
							this.getTemplate().insertText("Soft Fail Exception" + e.getMessage(), bookmark);
						} else {
							throw e;
						}
					}
				}
			}
		}
	}

	public String getNotLast() {
		return String.join(",", this.notLast);
	}

	public void setNotLast(String notLast) {
		this.notLast = new ArrayList<String>(Arrays.asList(notLast.split(",")));
	}

	public String getOnlyLast() {
		return String.join(",", this.onlyLast);
	}

	public void setOnlyLast(String onlyLast) {
		this.onlyLast = new ArrayList<String>(Arrays.asList(onlyLast.split(",")));
	}
}
