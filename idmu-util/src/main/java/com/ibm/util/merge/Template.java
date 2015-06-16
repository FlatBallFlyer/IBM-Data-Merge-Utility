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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.util.merge.ConnectionFactory;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.*;
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
public class Template implements Cloneable {
	// Global Constants (Reserved Tags)
	public static final String 	LFT 				= "{";
	public static final String	RGT 				= "}";
	public static final String 	TAG_STACK			= wrap("DragonFlyTemplateStack");
	public static final String 	TAG_ALL_VALUES		= wrap("DragonFlyReplaceValues");
	public static final String 	TAG_OUTPUTFILE		= wrap("DragonFlyOutputFile");
	public static final String 	TAG_SOFTFAIL		= wrap("DragonFlySoftFail");
	public static final String 	TAG_OUTPUT_TYPE		= wrap("DragonOutputType");
	public static final String 	TAG_SEQUENCE		= wrap("DragonSequence");
	public static final Pattern BOOKMARK_PATTERN 	= Pattern.compile("(<tkBookmark.*?/>)");
	
	// Template Constants
	private static final Logger 	log = Logger.getLogger( Template.class.getName() );

	// Attributes
	private transient long 						idtemplate		= 0;
	private String 								collection		= "";
	private String 								columnValue		= "";
	private String 								name			= "";
	private String 								description		= "";
	private String 								outputFile		= "";
	private StringBuilder 						content			= new StringBuilder();
	private List<Directive> 					directives		= new ArrayList<Directive>();
	private transient List<Bookmark> 			bookmarks		= new ArrayList<Bookmark>();
	private transient HashMap<String,String> 	replaceValues	= new HashMap<String,String>();

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
	 * Simple No-Parms Constructor 
	 */
	public Template() {
	}

	/**********************************************************************************
	 * Template Clone constructor, used to get a copy of a cached template and initialize it
	 * for merge processing. Performs deep copy of all objects and collections and populates
	 * the replace values hash with initial values.  
	 *
	 * @param  seedReplace Initial replace hash
	 * @throws MergeException - Wrapper of clone not supported exceptions
	 */
	public Template clone(HashMap<String,String> seedReplace) throws MergeException {
		Template newTemplate = null;
		try {
			newTemplate = (Template) super.clone();
			newTemplate.replaceValues 	= new HashMap<String,String>();
			newTemplate.setContent(this.getContent());
	
			// Deep Copy Directives
			newTemplate.directives = new ArrayList<Directive>();
			for(Directive fromDirective : this.directives) { newTemplate.addDirective(fromDirective.clone()); }
			
			// Seed Replace Values
			newTemplate.replaceValues.putAll(seedReplace);
			
			// Make sure we have an output file name guid
			if (!newTemplate.replaceValues.containsKey(TAG_OUTPUTFILE)) {
				newTemplate.replaceValues.put(TAG_OUTPUTFILE, UUID.randomUUID().toString() 
						+ (getOutputType() == ZipFactory.TYPE_ZIP ? ".zip" : ".tar")); 
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

	/********************************************************************************
	 * <p>Merge Template. This method drives the merge process, processing directives to select data, 
	 * insert sub-templates, and perform search replace activities.</p>
	 *
	 * @return The merged template contents, or an empty string if an output file is specified
	 * @throws MergeException Data Source Errors
	 * @throws MergeException Directive Processing Errors
	 * @throws MergeException Save Output File errors
	 */
	public String merge() throws MergeException {
		log.info("Begin Template Merge for:" + this.getFullName() );

		// Process Directives
		for(Directive directive : this.directives ) {
			directive.executeDirective();
		}		
		
		// Clear out the all-values replace tag
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
	 * Write the contents of the template as an entry in the Merge Archive file 
	 * @throws MergeException on File write errors
	 * @throws MergeException on TAG_OUTPUTFILE not in replace stack
	 */
	public void saveOutputAs() throws MergeException  {
		// don't save /dev/null or empty file names
		if (this.isEmpty()) {return;}
		if (this.outputFile == "/dev/null") {return;}

	    // Make sure we have an output file defined
		if (!this.replaceValues.containsKey(TAG_OUTPUTFILE)) {
			throw new MergeException("System Tag Not Found", TAG_OUTPUTFILE);
		}
		
		// Build the file name and replace process it
		String fileName = this.replaceProcess(this.outputFile);

		// Write the output file
		ZipFactory.writeFile(this.getOutputFile(), fileName, this.content, this.getOutputType());
		return;
	 }

	/********************************************************************************
	 * Finalize Creation of ZIP file, should always be called after merge is complete. 
	 * @throws MergeException File Save errors 
	 */
	public void packageOutput() throws MergeException  {
		ZipFactory.closeStream(this.getOutputFile(), this.getOutputType());
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
			if (this.replaceValues.get(key).isEmpty()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
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
	 * set a replace value to the empty string ""
	 *  @param keys The list of keys to empty
	 */
	public void addEmptyReplace(List<String> keys) {
		for(String from : keys) {
			this.replaceValues.put(wrap(from), "");
		}		
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
	public int getOutputType() {
		if (this.replaceValues.containsKey(TAG_OUTPUT_TYPE) && 
				this.replaceValues.get(TAG_OUTPUT_TYPE).endsWith("zip") ) {
				return ZipFactory.TYPE_ZIP; 
		} 
		return ZipFactory.TYPE_TAR;
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
	 * @return the full name (i.e. collection + Name + Column) 
	 */
	public String getFullName() {
		return this.collection + "." + this.name + "." + this.columnValue;
	}
	
    /**
	 * as json - Serialize a Template from the cache 
	 * @return a jSon serialized Template
	 */
    public String asJson(boolean bePretty) {
    	GsonBuilder builder = new GsonBuilder();
    	if (bePretty) builder.setPrettyPrinting();
     	Gson gson = builder.create();
    	return gson.toJson(this);
    }
	
	public List<Bookmark> getBookmarks() {
		return this.bookmarks;
	}

	public HashMap<String, String> getReplaceValues() {
		return this.replaceValues;
	}

	public long getIdtemplate() {
	    return this.idtemplate;
	}	
	
	public String getCollection() {
		return this.collection;
	}

	public void addDirective(Directive newDirective) {
		newDirective.setTemplate(this);
		newDirective.setIdTemplate(this.idtemplate);
		newDirective.setSequence(this.directives.size());
		this.directives.add(newDirective);
	}

	public String getColumnValue() {
		return this.columnValue;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String getContent() {
		return this.content.toString();
	}
	
	public String getOutput() {
		return this.outputFile;
	}

	public List<Directive> getDirectives() {
		return directives;
	}


	public void setContent(String content) throws MergeException {
		setContent(new StringBuilder(content));
	}
	/**
	 * setContent - initilize and parse Bookmarks collection from content
	 * @param content
	 * @throws MergeException
	 */
	public void setContent(StringBuilder content) throws MergeException {
		this.content = content;
		
		// Parse bookmarks array from text		
		Matcher m = BOOKMARK_PATTERN.matcher(this.content);
		this.bookmarks = new ArrayList<Bookmark>();
		while (m.find()) {
			this.bookmarks.add(new Bookmark( m.group(), m.start() ));
		}
	}

	public void setIdtemplate(long idtemplate) {
		this.idtemplate = idtemplate;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String HashCode() {
		return this.getFullName();
	}
	
	public boolean equals(Object obj) {
		Template that = (Template) obj;
		if (this.getFullName().equals(that.getFullName())) {
			return true;
		} else {
			return false;
		}
	}
}
