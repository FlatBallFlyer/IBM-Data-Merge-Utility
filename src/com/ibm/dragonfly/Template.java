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
import com.ibm.dragonfly.directive.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

/**********************************************************************************
 * This class represents a template and it's collection of directives. 
 * The merge method drives the template process.  
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 */
public class Template {
	private static final Logger log = Logger.getLogger( Template.class.getName() );
	private int    idtemplate;
	private String collection;
	private String columnValue;
	private String name;
	private String description;
	private StringBuilder content;
	private String outputFile;
	private String outputDir;
	private List<Bookmark> bookmarks 		= new ArrayList<Bookmark>();
	private List<InsertRows> insert			= new ArrayList<InsertRows>();
	private List<ReplaceRow> replaceRow		= new ArrayList<ReplaceRow>(); 
	private List<ReplaceColumn> replaceCol	= new ArrayList<ReplaceColumn>();
	private List<ReplaceValue> replaceVal	= new ArrayList<ReplaceValue>();
	private HashMap<String,String> replaceValues = new HashMap<String,String>();
 
	/**********************************************************************************
	 * <p>Template constructor</p>
	 *
	 * @param  Collection Name
	 * @param  Column Value
	 * @param  Template Name
	 * @throws tkException - Invalid Directive Type
	 * @return The new Template object
	 * @throws SQLException - Template Datasource errors
	 */
	public Template(String collection, String column, String name) throws tkException, tkSqlException {
		log.fine("Constructing Template " + collection + ", " + column + ", " + name);
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
		    	log.fine("Template not find by: " + queryString);
		    	queryString = getQueryString(collection, "", name);
		        rs = st.executeQuery(queryString);
		    }

		    // If still not found, throw tkException
		    if (!rs.isBeforeFirst()) {
		    	log.log(Level.SEVERE, "Template Not Found by: " + queryString);
		    	throw new tkException("Template Not Found by " + queryString, "Invalid Template Data");
		    }	    
		    rs.next();
		    this.idtemplate 	= rs.getInt("idtemplate");
			this.collection 	= rs.getString("collectionName");
			this.columnValue 	= rs.getString("columnValue");
			this.name 			= rs.getString("name");
			this.description 	= rs.getString("description");
			this.content 		= new StringBuilder(rs.getString("content"));
			this.outputFile 	= rs.getString("output");
			this.outputDir 		= System.getenv("DRAGONFLY_OUTPUT_ROOT");
			if ( this.outputDir == null ) {this.outputDir = "/tmp/output/";}
		} catch (SQLException e) {
			throw new tkSqlException("Template Database Error", "Invalid Template Data", queryString, e.getMessage());
		}
	    
		// load directives
		try {
		    rs = st.executeQuery("select * from directiveFull where idtemplate = " + this.idtemplate);	
			while (rs.next()) {
				String type = rs.getString("directiveType");
				log.finer("Loading Directive " + type);
				if ("Insert".equals(type)) { 
					this.insert.add(new InsertRows(rs)); 
				} else if ("ReplaceRow".equals(type)) 	{
					this.replaceRow.add(new ReplaceRow(rs)); 
				} else if ("ReplaceCol".equals(type)) 	{
					this.replaceCol.add(new ReplaceColumn(rs)); 
				} else if ("ReplaceVal".equals(type)) 	{
					this.replaceVal.add(new ReplaceValue(rs)); 
				} else if ("Require".equals(type)) 	{
					// Add support for Require directive 
				} else {
					throw new tkException("Invalid Directive Found: " + type, "Invalid Template Data");
				}
			}						
			con.close();
		} catch (SQLException e) {
			throw new tkSqlException("Template Database Error", "Invalid Directive Data", queryString, e.getMessage());
		}
	
		// Parse bookmarks array from text		
		log.fine("Parsing Bookmarks");
		Pattern p = Pattern.compile("(<tkBookmark.*/>)");
		Matcher m = p.matcher(this.content);
		while (m.find()) {
			this.bookmarks.add(new Bookmark( m.group(), m.start() ));
		}
		log.fine("Template " + this.getFullName() + " successfully constructed.");
	}

	/**********************************************************************************
	 * <p>Template Clone constructor, performs a deep copy of Bookmarks. 
	 * Directives are shared common objects (read only from construction)</p>
	 *
	 * @param  Template to clone
	 * @throws tkException - Invalid Directive Type
	 * @throws tkSqlException - Template Datasource errors
	 * @return The new Template object
	 */
	public Template(Template from) {
	    this.idtemplate 	= from.idtemplate;
		this.collection 	= from.collection;
		this.columnValue 	= from.columnValue;
		this.name 			= from.name;
		this.description 	= from.description;
		this.outputFile 	= from.outputFile;
		this.outputDir 		= from.outputDir;
		this.replaceVal 	= from.replaceVal;
		this.replaceCol 	= from.replaceCol;
		this.replaceRow 	= from.replaceRow;
		this.insert 		= from.insert;
		this.content 		= new StringBuilder(from.content);

		// Deep Copy Bookamrks		
		for(Bookmark fromBookmark : from.bookmarks) {
			Bookmark newBookmark = new Bookmark(fromBookmark);
			this.bookmarks.add(newBookmark);
		}		
	}

	/********************************************************************************
	 * <p>Merge Template. This method drives the merge process, processing directives to select data, 
	 * insert sub-templates, and perform search replace activities.</p>
	 *
	 * @return The merged template contents, or an empty string if an output file is specified
	 * @throws SQLException - Data Source Errors
	 * @throws IOException - Save Output File errors
	 */
	public String merge() throws tkException, tkSqlException, IOException {
		log.fine("Begin Template Merge for:" + this.getFullName() );

		// Process Directives
		for(ReplaceValue directive : this.replaceVal) {
			log.finer("Processing Replace Val directive");
			directive.getValues(this.replaceValues);
		}		
		for(ReplaceColumn directive : this.replaceCol) {
			log.finer("Processing Replace Column directive");
			directive.getValues(this.replaceValues);
		}		
		for(ReplaceRow directive : this.replaceRow) {
			log.finer("Processing Replace Row directive");
			directive.getValues(this);
		}		
		for(InsertRows directive : this.insert) {
			log.finer("Processing Insert Rows directive");
			directive.insertTemplates(this);
		}		
		
		// Clear out template replace values
		if ( this.replaceValues.get("tkReplaceValues") != null ) {
			this.replaceValues.put("{tkTemplate}"		, "");
			this.replaceValues.put("{tkCollection}"		, "");
			this.replaceValues.put("{tkColumnValue}"	, "");
			this.replaceValues.put("{tkDescription}"	, "");
			this.replaceValues.put("{tkReplaceValues}"	, "");			
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

		// Process "tkTemplate" replace values
		this.replaceThis("{tkTemplate}", 		this.name);
		this.replaceThis("{tkCollection}", 		this.collection);
		this.replaceThis("{tkColumnValue}", 	this.columnValue);
		this.replaceThis("{tkDescription}", 	this.description);
		this.replaceThis("{tkReplaceValues}", 	allValues);
		
		// Remove all the bookmarks
		this.replaceAllThis(Pattern.compile("<tkBookmark.*/>"), "");
	
		log.fine("Merge Complete: " + this.getFullName());
		
		// save the output file if specified
		if ( !this.outputFile.isEmpty() ) {
			this.saveAs();
			return "";			
		// or return the document
		} else {
			return this.content.toString();
		}
	}

	/********************************************************************************
	 * <p>Save As. Write the contents to disc, creating directories as needed</p> 
	 * @throws IOException - File creation and writing errors
	 *
	 * @throws IOException - File Save errors 
	 */
	public void saveAs() throws IOException  {
		// don't save /dev/null or empty file names
		if (this.isEmpty()) {return;}
		if (this.outputFile == "/dev/null") {return;}

		// Build the file name and process the replace stack
		String fileName = this.outputDir + this.outputFile;
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			fileName = fileName.replace(entry.getKey(), entry.getValue());
		}

	    // if file doesnt exists, then create it 
		File file = new File( fileName );
	    if ( ! file.exists( ) ) { 
	    	file.getParentFile().mkdirs();
	    	file.createNewFile( );
	    }

	    // write the content to the file 
	    log.fine("Writing Output File " + fileName);
	    FileWriter fw = new FileWriter( file.getAbsoluteFile( ) );
	    BufferedWriter bw = new BufferedWriter( fw );
	    bw.write( this.content.toString() );
	    bw.close( );
		return;
	 }

	/********************************************************************************
	 * <p>InsertText - Used to insert sub-templates and update bookmark offsets</p> 
	 *
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
	 * <p>Replace all occurances of <i>from</i> with <i>to</i></p> 
	 */
	public void replaceThis(String from, String to) {
		// Infinite loop safety
		if (from.isEmpty()) {return;}	
		to = to.replace("{tkReplaceValues}", "{- tkReplaceValues}"); // safety
		if ( to.lastIndexOf(from) > 0 ) {
			log.warning("Replace Attempted with Replace Value containg Replace Key:" + from + "\n Value:" + to);
			String fromDistored = "{**" + from.substring(1);
			to = fromDistored + " NOT REPLACED - TO Value COMTAINS " + fromDistored;
		}
		
		// do the replace
	    int index = -1;
	    while ((index = this.content.lastIndexOf(from)) != -1) {
	        this.content.replace(index, index + from.length(), to);
	    }
	}
	
	/********************************************************************************
	 * <p>Replace all this.content from Pattern to To</p> 
	 */
	public void replaceAllThis(Pattern pattern, String to) {
	    Matcher m = pattern.matcher(this.content);
	    this.content.replace(0, this.content.length(), m.replaceAll(to));
	}

	/********************************************************************************
	 * <p>addRowReplace - Adds the {colum}=>value replaces for a result set row</p> 
	 * @throws SQLException - Invalid result set or row 
	 *
	 * @throws SQLException on Data Source Errors 
	 */
	public void addRowReplace(ResultSet rs) throws SQLException  {
		ResultSetMetaData meta = rs.getMetaData();
		final int columnCount = meta.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) 
	    {
	    	String value = rs.getString(column);
	    	String colName = "{" + meta.getColumnLabel(column) + "}"; 
	        if (value != null) {
	            this.replaceValues.put(colName, value);
	        } else {
	        	this.replaceValues.put(colName, "");
	        }
	    }
	}

	/********************************************************************************
	 * <p>addEmptyReplace is used to set a list of replace values to ""</p> 
	 */
	public void addEmptyReplace(List<String> keys) {
		for(String from : keys) {
			this.replaceValues.put(from, "");
		}		
	}

	/********************************************************************************
	 * <p>Content is empty (whitespace)</p> 
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
	 * <p>Build a select statement for a Template, omit column if empty.</p>
	 *
	 * @return SQL Select Statement
	 */
	private String getQueryString(String collection, String column, String name) {
		if (column == null || column.isEmpty()) {
			return "select * from templatefull where collectionName = '" + collection + "'" + 
					" and columnValue = ''" +
					" and name = '" + name + "'";		
		} else {
		    return "select * from templatefull where collectionName = '" + collection + "'" + 
		    		" and columnValue = '" + column + "'" +  
		    		" and name = '" + name + "'";			
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
