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
import com.ibm.util.merge.ConnectionFactory;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
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
 * @Entity
 * @Table( name = "TEMPLATE" )
 */
public class Template {
	// Global Constants
	public static final String 	LFT 				= "{";
	public static final String	RGT 				= "}";
	public static final String 	TAG_STACK			= wrap("DragonFlyTemplateStack");
	public static final String 	TAG_ALL_VALUES		= wrap("DragonFlyReplaceValues");
	public static final String 	TAG_OUTPUTFILE		= wrap("DragonFlyOutputFile");
	public static final String 	TAG_SOFTFAIL		= wrap("DragonFlySoftFail");
	public static final String 	TAG_OUTPUT_TYPE		= wrap("DragonOutputType");
	
	// Template Constants
	private static final Logger 	log = Logger.getLogger( Template.class.getName() );
	private static final Pattern 	BOOKMARK_PATTERN 	= Pattern.compile("(<tkBookmark.*/>)");

	// Instance Variables
	private int    					idtemplate;
	private String 					collection	= "";
	private String 					columnValue	= "";
	private String 					name		= "";
	private String 					description;
	private StringBuilder 			content;
	private String 					outputFile;
	private List<Bookmark> 			bookmarks 	= new ArrayList<Bookmark>();
	private List<Directive> 		directives 	= new ArrayList<Directive>();
	private HashMap<String,String> 	replaceValues 	= new HashMap<String,String>();

	/********************************************************************************
	 * Static Helper to wrap a value in the Tag brackets
	 * @return String (wrappted tag)
	 * @param  value The value to wrap
	 * @return String the wrapped tag
	 */
	public static String wrap(String value) {
		return LFT + value + RGT;
	}

	
	/**********************************************************************************
	 * Template constructor that reads data from templateDB. 
	 *
	 * @param  collection Collection Name
	 * @param  column Column Value
	 * @param  name Template Name
	 * @throws MergeException Load Data from Database Errors
	 */
	public Template(String collection, String column, String name) throws MergeException {
		log.warn("Constructing Template " + collection + ", " + column + ", " + name);
		Connection con;
		Statement st;
		ResultSet rs;
		String queryString = "select * from templatefull where collectionName = '" + collection + "'" + 
				" and columnValue = '" + ((column == null || column.isEmpty()) ? "" : column) + "'" + 
				" and name = '" + name + "'";
		try {
			// Get a database connection from the pool
			con = ConnectionFactory.getTemplateConnection();
		    st = con.createStatement();

		    // Read the template record
		    rs = st.executeQuery(queryString);	

		    // If not found, look for the "default" template column value
		    if (!rs.isBeforeFirst()) {
		    	log.info("First Template not found by: " + queryString);
		    	queryString = "select * from templatefull where collectionName = '" + collection + "'" + 
						" and columnValue = '' " + 
						" and name = '" + name + "'";
		        rs = st.executeQuery(queryString);
		    }

		    // If still not found, throw tkException
		    if (!rs.isBeforeFirst()) {
		    	log.fatal("Second Template Not Found by: " + queryString);
		    	throw new MergeException("Template Not Found by " + queryString, "Invalid Template Data");
		    }	    
		    
		    // Instantiate the instance
		    rs.next();
/*		    this.idtemplate 	= rs.getInt(Template.COL_ID);
			this.collection 	= rs.getString(Template.COL_COLLECTION);
			this.columnValue 	= rs.getString(Template.COL_COLUMN);
			this.name 			= rs.getString(Template.COL_NAME);
			this.description 	= rs.getString(Template.COL_DESCRIPTION);
			this.content 		= new StringBuilder(rs.getString(Template.COL_CONTENT));
			this.outputFile 	= rs.getString(Template.COL_OUTPUT); */
		} catch (SQLException e) {
			throw new MergeException(e, "System Error - Template Database SQL Fault", queryString);
		}
	    
		// load directives
		try {
		    rs = st.executeQuery("select * from directivefull where idtemplate = " + this.idtemplate);	
			while (rs.next()) {
				int type = rs.getInt("TYPE");
				Directive newDirective = newDirective(type,rs);
				if (newDirective != null) {
					this.directives.add(newDirective);
				}
			} 	
			con.close();
		} catch (SQLException e) {
			throw new MergeException(e, "System Error - Template Database - Load Directives", this.getFullName());
		}
	
		// Parse bookmarks array from text		
		Matcher m = BOOKMARK_PATTERN.matcher(this.content);
		while (m.find()) {
			this.bookmarks.add(new Bookmark( m.group(), m.start() ));
		}
	}

	/**********************************************************************************
	 * <p>Simple helper to construct the Directive object based on the Type provided</p>
	 *
	 * @param  int Template Type (from Constants TYPE_*)
	 * @param  rs Sql ResultSet Row containing Directive data
	 * @throws MergeExeception - Constructor Errors
	 */
	private Directive newDirective(int type, ResultSet rs) throws MergeException {
		switch(type) {
			case Directive.TYPE_REQUIRE:
				return new Require(rs, this);
			case Directive.TYPE_REPLACE_VALUE: 
				return new ReplaceValue(rs, this);
			case Directive.TYPE_IF_INSERT: 
				return new InsertSubsIf(rs, this);
			case Directive.TYPE_SQL_INSERT: 
				return new InsertSubsSql(rs, this);
			case Directive.TYPE_SQL_REPLACE_ROW: 
				return new ReplaceRowSql(rs, this);
			case Directive.TYPE_SQL_REPLACE_COL: 	
				return new ReplaceColSql(rs, this);
			case Directive.TYPE_CSV_INSERT: 
				return new InsertSubsCsv(rs, this);
			case Directive.TYPE_CSV_REPLACE_ROW: 
				return new ReplaceRowCsv(rs, this);
			case Directive.TYPE_CSV_REPLACE_COL: 
				return new ReplaceColCsv(rs, this);
			case Directive.TYPE_HTML_INSERT: 
				return new InsertSubsHtml(rs, this);
			case Directive.TYPE_HTML_REPLACE_ROW: 
				return new ReplaceRowHtml(rs, this);
			case Directive.TYPE_HTML_REPLACE_COL: 
				return new ReplaceColHtml(rs, this);
			case Directive.TYPE_HTML_REPLACE_MARKUP: 
				return new ReplaceMarkupHtml(rs, this);
			default: 
				throw new MergeException("Invalid Directive Type: ", Integer.toString(type));
		} // end Switch type
	}

	/**********************************************************************************
	 * <p>Template Clone constructor, initializes replace values based on the passed parameter 
	 * and performs a deep copy of Bookmarks and Directives. Initialization of the output file name
	 * and construction of the template stack are performed in the clone as well. </p>
	 *
	 * @param  seedReplace Initial replace hash
	 * @throws MergeException - Wrapper of clone not supported exceptions
	 */
	public Template clone(HashMap<String,String> seedReplace) throws MergeException {
		Template newTemplate = null;
		try {
			newTemplate = (Template) super.clone();
			newTemplate.replaceValues 	= new HashMap<String,String>();
			newTemplate.content 		= new StringBuilder(this.content);
	
			// Deep Copy Collections
			newTemplate.bookmarks		= new ArrayList<Bookmark>();
			for(Bookmark fromBkm : this.bookmarks) 	{ newTemplate.bookmarks.add(fromBkm.clone()); }
			newTemplate.directives = new ArrayList<Directive>();
			for(Directive fromDirective : this.directives) { newTemplate.directives.add(fromDirective.clone(this)); }
			
			// Seed Replace Values
			newTemplate.replaceValues.putAll(seedReplace);
			
			// Make sure we have an output file name guid
			if (!newTemplate.replaceValues.containsKey(TAG_OUTPUTFILE)) {
				newTemplate.replaceValues.put(TAG_OUTPUTFILE, UUID.randomUUID().toString() 
						+ (getOutputType() == ZipFactory.TYPE_ZIP ? ".zip" : ".tar.gz")); 
			}
			
			// Add our name to the Template Stack
			if (!newTemplate.replaceValues.containsKey(TAG_STACK)) {
				newTemplate.replaceValues.put(TAG_STACK, this.getFullName()); 
			} else {
				newTemplate.replaceValues.put(TAG_STACK, newTemplate.replaceValues.get(TAG_STACK) + "/" + newTemplate.getFullName());
			}
		} catch (CloneNotSupportedException e) {
			throw new MergeException(e, "System Error", "Clone Not Supported");
		}
		
		return newTemplate;
	}

	/**
	 * Persist Template and Directives to TemplateDB
	 */
	public void saveToDb() {
		// TODO Delete from template where name=this.name and collection = this.collection and column = this.column
		// 
		// this.id = this.insert()
		// for (Directive directive : this.directives) {
		//		directive.save();
		// }
	}

	/********************************************************************************
	 * <p>Merge Template. This method drives the merge process, processing directives to select data, 
	 * insert sub-templates, and perform search replace activities.</p>
	 *
	 * @return The merged template contents, or an empty string if an output file is specified
	 * @throws MergeException Data Source Errors
	 * @throws MergeException Directive Processing Errors
	 * @throws IOException Save Output File errors
	 */
	public String merge() throws MergeException {
		log.info("Begin Template Merge for:" + this.getFullName() );

		// Process Directives
		for(Directive directive : this.directives ) {
			directive.executeDirective();
		}		
		
		// Clear out template all-values replace tag
		this.replaceValues.remove(TAG_ALL_VALUES);			
		
		// Process Replace Stack and build "All Values" replace value  
		String allValues = "";
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			this.replaceThis(entry.getKey(), entry.getValue());
			String from = "{-" + entry.getKey().substring(1);
			allValues += from + "->" + entry.getValue() + "\n";
		}

		// Process Replace Stack again (to support Nested Tags)
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			this.replaceThis(entry.getKey(), entry.getValue());
		}
		
		// Replace the all values tag
		this.replaceThis(TAG_ALL_VALUES, allValues);
		
		// Remove all the bookmarks
		this.replaceAllThis(BOOKMARK_PATTERN, "");
	
		log.info("Merge Complete: " + this.getFullName());
		
		// save the output file or return the content
		if ( !this.outputFile.isEmpty() ) {
			this.saveOutputAs();
			return "";			
		} else {
			return this.content.toString();
		}
	}

	/********************************************************************************
	 * <p>Save As. Write the contents to the Zip file</p> 
	 * @throws IOException Zip File writing errors
	 * @throws MergeException TAG_OUTPUTFILE not in replace stack
	 */
	public void saveOutputAs() throws MergeException  {
		// don't save /dev/null or empty file names
		if (this.isEmpty()) {return;}
		if (this.outputFile == "/dev/null") {return;}

		// Build the file name and process the replace stack
		String fileName = this.outputFile;
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			fileName = fileName.replace(entry.getKey(), entry.getValue());
		}

	    // Create a file entry in the output zip.
		if (!this.replaceValues.containsKey(TAG_OUTPUTFILE)) {
			throw new MergeException("System Tag Not Found", TAG_OUTPUTFILE);
		}
		
		try {
			ZipOutputStream out = ZipFactory.getZipStream(this.getOutputFile(), this.getOutputType());
			ZipEntry entry = new ZipEntry(fileName);
			out.putNextEntry(entry);
		    log.info("Writing Output File " + fileName);
		    out.write(this.content.toString().getBytes()); 
		    out.closeEntry();
		} catch (FileNotFoundException e) {
			throw new MergeException("FileNotFound", fileName);
		} catch (IOException e) {
			throw new MergeException("File IO Error", fileName);
		}
		
	    // write the content to the file 
		return;
	 }

	/********************************************************************************
	 * Finalize Creation of ZIP file, should always be called after merge is complete. 
	 * @throws IOException File Save errors 
	 * @throws MergeException 
	 */
	public void packageOutput() throws MergeException  {
		ZipFactory.closeStream(this.getOutputFile());
		ConnectionFactory.close(this.getOutputFile());
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
	 * @throws MergeException Replace To contains From value
	 */
	public void replaceThis(String from, String to) throws MergeException {
		// don't waste time
		if (from.isEmpty()) {return;}	

		// Infinite loop safety
		to = to.replace(TAG_ALL_VALUES, "{ALL VALUES TAG}"); // all values tag safety
		if ( to.lastIndexOf(from) > 0 ) {
			String message = "Replace Attempted with Replace Value containg Replace Key:" + from + "\n Value:" + to + " in " + this.getFullName(); 
			if ( this.softFail() ) {
				log.warn("SOFT FAIL -" + message);
				to = "SOFT FAIL - VALUE NOT REPLACED, TO CONTAINS FROM";
			} else {
				throw new MergeException(message, this.getStack());
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
	 * addReplace Add a from-to pair with a wrapped from value
	 * 
	 *  @param from The from value
	 *  @param to The replace value
	 */
	public void addReplace(String from, String to) {
		this.replaceValues.put( wrap(from), to);
	}

	/********************************************************************************
	 * Process the replace hash on the provided string
	 * 
	 *  @param value The string to process
	 *  @return The string after processing the replace string
	 */
	public String replaceProcess(String value) {
		for (Map.Entry<String, String> entry : this.replaceValues.entrySet()) {
			value = value.replace(entry.getKey(), entry.getValue());
		}
		return value;
	}

	/********************************************************************************
	 * Content is empty (whitespace)
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
	 * set a replace value to the empty string ""
	 *  @param keys The list of keys to empty
	 */
	public void addEmptyReplace(List<String> keys) {
		for(String from : keys) {
			this.replaceValues.put(wrap(from), "");
		}		
	}

	/********************************************************************************
	 * does the replace hash contain a specific key
	 *  @param key The key of the entry to get
	 *  @return boolean True if the key exists 
	 */
	public boolean hasReplaceKey(String key) {
		return this.replaceValues.containsKey(key);
	}

	/********************************************************************************
	 * does the replace hash contain a specific key, with a non-blank value.
	 *  @param key The key of the entry to get
	 *  @return boolean True if the key exists with a non-empty value.
	 */
	public boolean hasReplaceValue(String key) {
		if (this.replaceValues.containsKey(key)) {
			return ! this.replaceValues.get(key).isEmpty();
		}
		return false;
	}
	

	/********************************************************************************
	 * get a value from the replace hash
	 *  @param key The key of the entry to get
	 *  @return value, the value of that entry.
	 *  @throws MergeException when value not found.
	 *  @see hasReplaceKey and hasReplaceValue()
	 */
	public String getReplaceValue(String key) throws MergeException {
		if (this.replaceValues.containsKey(key)) {
			return this.replaceValues.get(key);
		} else {
			String msg = "Replace Value for key " + key + " Was not found!";
			throw new MergeException(msg, this.getFullName());
		}		
	}
	
	/**
	 * @return soft fail indicator (from replace hash)
	 */
	public boolean softFail() {
		return this.replaceValues.containsKey(TAG_SOFTFAIL);
	}

	/**
	 * @return the Template Stack (from replace hash)
	 */
	public String getStack() throws MergeException {
		return this.getReplaceValue(TAG_STACK);
	}
	
	/**
	 * @return the Output File name (from replace hash)
	 */
	public String getOutputFile() throws MergeException  {
		return this.getReplaceValue(TAG_OUTPUTFILE);
	}

	/**
	 * @return output type indicator (Default to GZIP, allow "zip" over-ride
	 */
	private int getOutputType() {
		if (this.replaceValues.containsKey(TAG_OUTPUT_TYPE) && 
				this.replaceValues.get(TAG_OUTPUT_TYPE) == "zip") {
				return ZipFactory.TYPE_ZIP; 
		} 
		return ZipFactory.TYPE_GZIP;
	}

	/**
	 * @return the full name 
	 * TODO - Consider full names as collection, name, columnValue
	 */
	public String getFullName() {
		return this.collection + ":" + this.columnValue + ":" + this.name;
	}
	
	/**
	 * @return the Bookmark List
	 */
	public List<Bookmark> getBookmarks() {
		return this.bookmarks;
	}

	/**
	 * @return the Replace Hash
	 */
	public HashMap<String, String> getReplaceValues() {
		return this.replaceValues;
	}


	/**
	 * @return the template ID
	 * @Id
	 * @GeneratedValue(generator="increment")
	 * @GenericGenerator(name="increment", strategy = "increment")
	 * @Column(name = "idtemplate");
	 */
	public int getId() {
	    return this.idtemplate;
	}	
	
	/**
	 * @return the collection
	 * @Column(name = "collectionName");
	 */
	public String getCollection() {
		return this.collection;
	}

	/**
	 * @return the columnValue
	 * @Column(name = "columnValue)";
	 */
	public String getColumnValue() {
		return this.columnValue;
	}

	/**
	 * @return the name
	 * @Column(name = "name)";
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return
	 * @Column(name = "description)";
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return
	 * @Column(name = "content)";
	 */
	public String getContent() {
		return this.content.toString();
	}
	
	/**
	 * @return the output file spec
	 * @Column(name = "output)";
	 */
	public String getOutput() {
		return this.outputFile;
	}

}
