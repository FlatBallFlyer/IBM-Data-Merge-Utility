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
import com.ibm.dragonfly.ConnectionFactory;
import com.ibm.dragonfly.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**********************************************************************************
 * A template and it's collection of directives. This class represents the primary 
 * interface for DragonFly. 
 *  
 * @see Merge 
 * @see #merge()
 * @see #packageOutput()
 * @author  Mike Storey
 */
public class Template {
	// Global Constants
	public static final String 	LFT 				= "{";
	public static final String	RGT 				= "}";
	public static final String 	TAG_STACK			= wrap("DragonFlyTemplateStack");
	public static final String 	TAG_ALL_VALUES		= wrap("DragonFlyReplaceValues");
	public static final String 	TAG_OUTPUTFILE		= wrap("DragonFlyOutputFile");
	public static final String 	TAG_SOFTFAIL		= wrap("DragonFlySoftFail");
	
	// Template Constants
	private static final Logger 	log = Logger.getLogger( Template.class.getName() );
	private static final String		TYPE_INSERT 		= "Insert";
	private static final String 	TYPE_REPLACE_ROW 	= "ReplaceRow";
	private static final String 	TYPE_REPLACE_COL 	= "ReplaceCol";
	private static final String 	TYPE_REPLACE_VAL 	= "ReplaceVal";
	private static final String 	TYPE_REQUIRE 		= "Require";
	private static final Pattern 	BOOKMARK_PATTERN 	= Pattern.compile("(<tkBookmark.*/>)");


	/********************************************************************************
	 * Static Helper to wrap a value in the Tag brackets
	 * @return String (wrappted tag)
	 * @param  value The value to wrap
	 * @return String the wrapped tag
	 */
	public static String wrap(String value) {
		return LFT + value + RGT;
	}

	// Instance Variables
	private int    					idtemplate;
	private String 					collection;
	private String 					columnValue;
	private String 					name;
	private String 					description;
	private StringBuilder 			content;
	private String 					outputFile;
	private List<Bookmark> 			bookmarks 		= new ArrayList<Bookmark>();
	private List<InsertRows> 		insert			= new ArrayList<InsertRows>();
	private List<ReplaceRow> 		replaceRow		= new ArrayList<ReplaceRow>(); 
	private List<ReplaceColumn> 	replaceCol		= new ArrayList<ReplaceColumn>();
	private List<ReplaceValue> 		replaceVal		= new ArrayList<ReplaceValue>();
	private HashMap<String,String> 	replaceValues 	= new HashMap<String,String>();

	
	/**********************************************************************************
	 * Template constructor that reads data from templateDB. 
	 *
	 * @param  collection Collection Name
	 * @param  column Column Value
	 * @param  name Template Name
	 * @throws DragonFlyException Invalid Directive Type
	 * @throws DragonFlySqlException Template Datasource errors
	 */
	public Template(String collection, String column, String name) throws DragonFlyException, DragonFlySqlException {
		log.warn("Constructing Template " + collection + ", " + column + ", " + name);
		Connection con;
		Statement st;
		ResultSet rs;
		String queryString = "";
		try {
			// Get a database connection from the pool
			con = ConnectionFactory.getTemplateConnection();
		    st = con.createStatement();

		    // Read the template record
		    queryString = getQueryString(collection, column, name);
		    rs = st.executeQuery(queryString);	

		    // If not found, look for the "default" template column value
		    if (!rs.isBeforeFirst()) {
		    	log.info("First Template not found by: " + queryString);
		    	queryString = getQueryString(collection, "", name);
		        rs = st.executeQuery(queryString);
		    }

		    // If still not found, throw tkException
		    if (!rs.isBeforeFirst()) {
		    	log.fatal("Second Template Not Found by: " + queryString);
		    	throw new DragonFlyException("Template Not Found by " + queryString, "Invalid Template Data");
		    }	    
		    rs.next();
		    this.idtemplate 	= rs.getInt("idtemplate");
			this.collection 	= rs.getString("collectionName");
			this.columnValue 	= rs.getString("columnValue");
			this.name 			= rs.getString("name");
			this.description 	= rs.getString("description");
			this.content 		= new StringBuilder(rs.getString("content"));
			this.outputFile 	= rs.getString("output");
		} catch (SQLException e) {
			throw new DragonFlySqlException("Template Database Error", "Invalid Template Data", queryString, e.getMessage());
		}
	    
		// load directives
		try {
		    rs = st.executeQuery("select * from directivefull where idtemplate = " + this.idtemplate);	
			while (rs.next()) {
				String type = rs.getString("directiveType");
				log.info("Loading Directive " + type);
				if (TYPE_INSERT.equals(type)) { 
					this.insert.add(new InsertRows(rs)); 
				} else if (TYPE_REPLACE_ROW.equals(type)) 	{
					this.replaceRow.add(new ReplaceRow(rs)); 
				} else if (TYPE_REPLACE_COL.equals(type)) 	{
					this.replaceCol.add(new ReplaceColumn(rs)); 
				} else if (TYPE_REPLACE_VAL.equals(type)) 	{
					this.replaceVal.add(new ReplaceValue(rs)); 
				} else if (TYPE_REQUIRE.equals(type)) 	{
					// Add support for Require directive 
				} else {
					throw new DragonFlyException("Invalid Directive Found: " + type, "Invalid Template Data");
				}
			}						
			con.close();
		} catch (SQLException e) {
			throw new DragonFlySqlException("Template Database Error", "Invalid Directive Data", queryString, e.getMessage());
		}
	
		// Parse bookmarks array from text		
		Matcher m = BOOKMARK_PATTERN.matcher(this.content);
		while (m.find()) {
			this.bookmarks.add(new Bookmark( m.group(), m.start() ));
		}
	}

	/**********************************************************************************
	 * <p>Template Clone constructor, performs a deep copy of Bookmarks. 
	 * Directives are shared common objects (read only from construction)</p>
	 *
	 * @param  from Template to clone
	 * @param  seedReplace Initial replace hash
	 */
	public Template(Template from, HashMap<String,String> seedReplace) {
	    this.idtemplate 	= from.idtemplate;
		this.collection 	= from.collection;
		this.columnValue 	= from.columnValue;
		this.name 			= from.name;
		this.description 	= from.description;
		this.outputFile 	= from.outputFile;
		this.replaceValues 	= new HashMap<String,String>();
		this.content 		= new StringBuilder(from.content);

		// Deep Copy Collections
		this.bookmarks		= new ArrayList<Bookmark>();
		for(Bookmark 	 	fromBkm : from.bookmarks) 	{ this.bookmarks.add(new Bookmark(fromBkm)); 		}

		this.replaceVal 	= from.replaceVal;
		//this.replaceVal 	= new ArrayList<ReplaceValue>();
		//for(ReplaceValue 	fromRep : from.replaceVal) 	{ this.replaceVal.add(new ReplaceValue(fromRep)); 	}
		this.replaceCol 	= from.replaceCol;
		//this.replaceCol 	= new ArrayList<ReplaceColumn>();
		//for(ReplaceColumn	fromCol : from.replaceCol) 	{ this.replaceCol.add(new ReplaceColumn(fromCol)); 	}
		this.replaceRow 	= from.replaceRow;
		//this.replaceRow 	= new ArrayList<ReplaceRow>();
		//for(ReplaceRow	 	fromRow : from.replaceRow) 	{ this.replaceRow.add(new ReplaceRow(fromRow)); 	}
		this.insert 		= from.insert;
		//this.insert 		= new ArrayList<InsertRows>();
		//for(InsertRows 	 	fromIns : from.insert) 		{ this.insert.add(new InsertRows(fromIns)); 		}


		
		// Load Initial Replace Values
		this.replaceValues.putAll(seedReplace);
		
		// Make sure we have an output dir guid
		if (!this.replaceValues.containsKey(TAG_OUTPUTFILE)) {
			this.replaceValues.put(TAG_OUTPUTFILE, UUID.randomUUID().toString() + ".zip"); 
		}
		
		// Add our name to the Template Stack
		if (!this.replaceValues.containsKey(TAG_STACK)) {
			this.replaceValues.put(TAG_STACK, this.getFullName()); 
		} else {
			this.replaceValues.put(TAG_STACK, this.replaceValues.get(TAG_STACK) + "/" + this.getFullName());
		}
		
	}
	
	/********************************************************************************
	 * <p>Merge Template. This method drives the merge process, processing directives to select data, 
	 * insert sub-templates, and perform search replace activities.</p>
	 *
	 * @return The merged template contents, or an empty string if an output file is specified
	 * @throws DragonFlySqlException Data Source Errors
	 * @throws DragonFlyException Directive Processing Errors
	 * @throws IOException Save Output File errors
	 */
	public String merge() throws DragonFlyException, DragonFlySqlException, IOException {
		log.info("Begin Template Merge for:" + this.getFullName() );

		// Process Directives
		for(ReplaceValue directive : this.replaceVal) {
			directive.getValues(this);
		}		
		for(ReplaceColumn directive : this.replaceCol) {
			directive.getValues(this);
		}		
		for(ReplaceRow directive : this.replaceRow) {
			directive.getValues(this);
		}		
		for(InsertRows directive : this.insert) {
			directive.getValues(this);
		}		
		
		// Clear out template all-values replace tag
		if ( this.replaceValues.get(TAG_ALL_VALUES) != null ) {
			this.replaceValues.put(TAG_ALL_VALUES, "");			
		}
		
		// Process Replace Stack  
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			this.replaceThis(entry.getKey(), entry.getValue());
		}

		// Process Replace Stack again (to support Nested Tags)
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			this.replaceThis(entry.getKey(), entry.getValue());
		}
		
		// Build "all values" replace string
		String allValues = "";
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			if ( !entry.getKey().isEmpty()) {
				String from = "{-" + entry.getKey().substring(1);
				allValues += from + "->" + entry.getValue() + "\n";
			}
		}

		// Replace the all values tag
		this.replaceThis(TAG_ALL_VALUES, allValues);
		
		// Remove all the bookmarks
		this.replaceAllThis(BOOKMARK_PATTERN, "");
	
		log.info("Merge Complete: " + this.getFullName());
		
		// save the output file or return the content
		if ( !this.outputFile.isEmpty() ) {
			this.saveAs();
			return "";			
		} else {
			return this.content.toString();
		}
	}

	/********************************************************************************
	 * <p>Save As. Write the contents to the Zip file</p> 
	 * @throws IOException Zip File writing errors
	 * @throws DragonFlyException TAG_OUTPUTFILE not in replace stack
	 */
	public void saveAs() throws IOException, DragonFlyException  {
		// don't save /dev/null or empty file names
		if (this.isEmpty()) {return;}
		if (this.outputFile == "/dev/null") {return;}

		// Build the file name and process the replace stack
		String fileName = this.outputFile;
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			fileName = fileName.replace(entry.getKey(), entry.getValue());
		}

	    // Create an file entry in the output zip.
		if (!this.replaceValues.containsKey(TAG_OUTPUTFILE)) {
			throw new DragonFlyException("System Tag Not Found", TAG_OUTPUTFILE);
		}
		ZipOutputStream out = ZipFactory.getZipStream(this.replaceValues.get(TAG_OUTPUTFILE));
		ZipEntry entry = new ZipEntry(fileName);
		out.putNextEntry(entry);
		
	    // write the content to the file 
	    log.info("Writing Output File " + fileName);
	    out.write(this.content.toString().getBytes()); 
	    out.closeEntry();
		return;
	 }

	/********************************************************************************
	 * Finalize Creation of ZIP file, should always be called after merge is complete. 
	 * @throws IOException File Save errors 
	 */
	public void packageOutput() throws IOException  {
		ZipFactory.closeStream(this.replaceValues.get(TAG_OUTPUTFILE));
		ConnectionFactory.close(this.replaceValues.get(TAG_OUTPUTFILE));
	}
	
	/********************************************************************************
	 * <p>InsertText Used to insert sub-templates and update bookmark offsets</p> 
	 *
	 * @param txt The text to insert
	 * @param bkm The bookmark at which to insert the text
	 */
	public void insertText(String txt, Bookmark bkm) {
		// don't insert only white-space
		if ( txt.matches("^\\s*$") ) {return;} 

		// Insert the text
		this.content.insert(bkm.getStart(), txt);

		// Shift bookmark starting points.
		for(Bookmark theBookmark : this.bookmarks) {
			if ( theBookmark.getStart() >= bkm.getStart() ) {
				theBookmark.offest(txt.length());
			}
		}
		return;
	}

	/********************************************************************************
	 * Replace all occurrences of a string within the template content
	 * 
	 * @param from The string to replace
	 * @param to The value to replace with
	 * @throws DragonFlyException Replace To contains From value
	 */
	public void replaceThis(String from, String to) throws DragonFlyException {
		// don't waste time
		if (from.isEmpty()) {return;}	

		// Infinite loop safety
		to = to.replace(TAG_ALL_VALUES, "{ALL VALUES TAG}"); // all values tag safety
		if ( to.lastIndexOf(from) > 0 ) {
			String message = "Replace Attempted with Replace Value containg Replace Key:" + from + "\n Value:" + to + " in " + this.getFullName(); 
			log.fatal(message);
			if ( replaceValues.containsKey(TAG_SOFTFAIL) ) {
				log.warn("SOFT FAIL -" + message);
				to = "SOFT FAIL - VALUE NOT REPLACED, TO CONTAINS FROM";
			} else {
				throw new DragonFlyException("Replace Error!", message);
			}
		}
		
		// do the replace
	    int index = -1;
	    while ((index = this.content.lastIndexOf(from)) != -1) {
	        this.content.replace(index, index + from.length(), to);
	    }
	}
	
	/********************************************************************************
	 * Replace all using RegEx Pattern
	 * 
	 *  @param pattern the Regex Pattern to search for
	 *  @param to The string to replace the pattern with
	 */
	public void replaceAllThis(Pattern pattern, String to) {
	    Matcher m = pattern.matcher(this.content);
	    this.content.replace(0, this.content.length(), m.replaceAll(to));
	}

	/********************************************************************************
	 * <p>addRowReplace Adds the {colum} to value replaces for a result set row</p> 
	 *
	 * @param rs The resulset to add
	 * @throws SQLException on Data Source Errors
	 */
	public void addRowReplace(ResultSet rs) throws SQLException  {
		ResultSetMetaData meta = rs.getMetaData();
		final int columnCount = meta.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) 
	    {
	    	String value = rs.getString(column);
	    	String colName = meta.getColumnLabel(column); 
	        if (value != null) {
	            this.replaceValues.put(wrap(colName), value);
	        } else {
	        	this.replaceValues.put(wrap(colName), "");
	        }
	    }
	}

	/********************************************************************************
	 * addColReplace Add a from-to pair with a wrapped from value
	 * 
	 *  @param from The from value
	 *  @param to The replace value
	 */
	public void addColReplace(String from, String to) {
		this.replaceValues.put( wrap(from), to);
	}

	/********************************************************************************
	 * set a replace value to the empty string ""
	 * 
	 *  @param keys The list of keys to empty
	 */
	public void addEmptyReplace(List<String> keys) {
		for(String from : keys) {
			this.replaceValues.put(wrap(from), "");
		}		
	}

	/********************************************************************************
	 * Content is empty (whitespace)
	 * 
	 *  @return boolean empty
	 */
	public boolean isEmpty() {
		for (int i=1; i < this.content.length(); i++) {
			if (!Character.isWhitespace(this.content.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/********************************************************************************
	 * Build a select statement for a Template, omit column if empty.
	 *
	 * @param collection Template Collection 
	 * @param column Template Column Value
	 * @param name Template Name
	 * @return SQL Select Statement
	 */
	private String getQueryString(String collection, String column, String name) {
		return "select * from templatefull where collectionName = '" + collection + "'" + 
				" and columnValue = '" + ((column == null || column.isEmpty()) ? "" : column) + "'" + 
				" and name = '" + name + "'";		
	}

	/********************************************************************************
	 * Get the TAG_OUTPUTFILE Guid from the replace stack
	 * @throws DragonFlyException  if Outputfile is not in the stack (should be impossible)
	 * @return The output file GUID
	 */
	public String getOutputFile() throws DragonFlyException  {
		if (this.replaceValues.containsKey(TAG_OUTPUTFILE)) {
			return this.replaceValues.get(TAG_OUTPUTFILE);
		} else {
			String msg = "TAG_OUTPUTFILE " + TAG_OUTPUTFILE + " Was not found!";
			log.error(msg);
			throw new DragonFlyException(msg, "Cat N");
		}
	}
	
	// - SIMPLE GETTER'S BELOW HERE -
	/**
	 * @return the full name
	 */
	public String getFullName() {
		return collection + ":" + columnValue + ":" + name;
	}

	/**
	 * @return the collection
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * @return the columnValue
	 */
	public String getColumnValue() {
		return columnValue;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the replaceValues
	 */
	public HashMap<String, String> getReplaceValues() {
		return replaceValues;
	}

	/**
	 * @return the bookmarks
	 */
	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}
	
	
}
