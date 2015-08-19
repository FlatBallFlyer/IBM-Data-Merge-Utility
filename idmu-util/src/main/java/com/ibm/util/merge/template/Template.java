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
package com.ibm.util.merge.template;

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**********************************************************************************
 * A template and it's collection of directives. This class represents the primary
 * interface for DragonFly.
 *
 * @author Mike Storey
 * @see Merge
 * @see #merge()
 * @see #packageOutput()
 */
public class Template {
    // Global Constants (Reserved Tags)
    public static final String LFT = "{";
    public static final String RGT = "}";
    public static final String TAG_STACK 		= wrap("DragonFlyTemplateStack");
    public static final String TAG_ALL_VALUES 	= wrap("DragonFlyReplaceValues");
    public static final String TAG_OUTPUTFILE 	= wrap("DragonFlyOutputFile");
    public static final String TAG_OUTPUTHASH 	= wrap("DragonFlyOutputHash");
    public static final String TAG_SOFTFAIL 	= wrap("DragonFlySoftFail");
    public static final String TAG_OUTPUT_TYPE 	= wrap("DragonFlyOutputType");
    public static final String TAG_SEQUENCE 	= wrap("DragonFlySequence");
    public static final String BOOKMARK_PATTERN_STRING = "(<tkBookmark.*?/>)";
    public static final Pattern BOOKMARK_PATTERN = Pattern.compile(BOOKMARK_PATTERN_STRING);
    // Factory Constants
    public static final String TYPE_ZIP = "zip";
    public static final String TYPE_TAR = "tar";
    // Template Constants
    private static final Logger log = Logger.getLogger(Template.class.getName());
    // Attributes
    private transient long idtemplate = 0;
    private String collection = "";
    private String name = "";
    private String columnValue = "";
    private String description = "";
    private String outputFile = "";
    private StringBuilder content = new StringBuilder();
    private List<AbstractDirective> directives = new ArrayList<>();
    private transient Boolean merged = false;
    private transient Boolean mergable = false;
    private transient List<Bookmark> bookmarks = new ArrayList<>();
    private transient Map<String, String> replaceValues = new HashMap<>();

    /********************************************************************************
     * Static Helper to wrap a value in the Tag brackets
     *
     * @param value The value to wrap
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
     * "Mergable" template constructor (copy of a template that is cache)
     *
     * @param seedReplace Initial replace hash
     * @throws MergeException - Wrapper of clone not supported exceptions
     */
    public Template getMergable(Map<String, String> seedReplace) {
    	Template to = new Template();
    	to.setIdtemplate(	this.getIdtemplate());
    	to.setCollection( 	this.getCollection());
    	to.setName(  		this.getName());
    	to.setColumnValue( 	this.getColumnValue());
    	to.setDescription( 	this.getDescription());
    	to.setOutputFile(  	this.outputFile);
    	to.setContent(		this.getContent());
        
    	to.setDirectives(  	new ArrayList<AbstractDirective>());
    	to.setMerged(  		false);
    	to.setMergable( 	true);
    	to.setReplaceValues(new HashMap<String, String>());

        // Deep Copy Directives
        for (AbstractDirective fromDirective : this.getDirectives()) {
        	if (fromDirective != null) {
            	to.addDirective(fromDirective.asNew());
        	}
        }

        // Seed the replace stack
        to.replaceValues.putAll(seedReplace);

        // Add our name to the Template Stack
        to.appendToReplaceValue(TAG_STACK, this.getFullName(), "/");
        
        return to;
    }

    public String getMergedOutput(MergeContext rtc) throws MergeException {
    	if (!this.mergable) {
    		throw new RuntimeException("Merge requested on Un-Mergable Template!");
    	}
    	
    	if (!this.merged) {
    		this.merge(rtc);
    	}
    	
    	if (this.outputFile.isEmpty()) {
    		return this.content.toString();
    	} else {
    		String fileName = this.replaceProcess(this.outputFile);
    		String content = this.content.toString();
    		try {
				rtc.writeFile(fileName, content);
			} catch (IOException e) {
				throw new MergeException(this, e, "Error writing output file", fileName);
			}
    		return "";
    	}
    }
    /********************************************************************************
     * <p>Merge Template. This method drives the merge process, processing directives to select data,
     * insert sub-templates, and perform search replace activities.</p>
     *
     * @return The merged template contents, or an empty string if an output file is specified
     * @throws MergeException Data Source Errors
     * @throws MergeException Directive Processing Errors
     * @throws MergeException Save Output File errors
     * @param rtc
     */
    public void merge(MergeContext rtc) throws MergeException {
    	if (this.merged) return;

    	log.info("Begin Template Merge for:" + getFullName());
        // Process Directives
        for (AbstractDirective directive : directives) {
            directive.executeDirective(rtc);
        }
        // Clear out the all-values replace tag
        replaceValues.remove(TAG_ALL_VALUES);

        // Get the output hash replace value
        replaceValues.put(TAG_OUTPUTHASH, rtc.getArchiveChkSums());

        // Process Replace Stack and build "All Values" replace value
        String allValues = "";
        for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
            replaceThis(entry.getKey(), entry.getValue());
            String from = "{-" + entry.getKey().substring(1);
            allValues += from + "->" + entry.getValue() + "\n";
        }
        // Process Replace Stack again (to support Nested Tags)
        for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
            replaceThis(entry.getKey(), entry.getValue());
        }
        // Replace the all values tag
        replaceThis(TAG_ALL_VALUES, allValues);
        // Replace the Output Hash tag
        replaceThis(TAG_OUTPUTHASH, "");
        // Remove all the bookmarks
        replaceAllThis(BOOKMARK_PATTERN, "");
        this.merged = true;
        log.info("Merge Complete: " + getFullName());
    }

    /********************************************************************************
     * <p>InsertText Used to insert sub-templates and update bookmark offsets</p>
     *
     * @param txt The text to insert
     * @param bkm The bookmark at which to insert the text
     */
    public void insertText(String txt, Bookmark bkm) {
        // don't insert only white-space
        if (!txt.trim().isEmpty()) {
            // Insert the text
            int start = bkm.getStart();
            content.insert(start, txt);
            // Shift bookmark starting points.
            for (Bookmark theBookmark : bookmarks) {
                if (theBookmark.getStart() >= start) {
                    theBookmark.offest(txt.length());
                }
            }
        }
    }

    /********************************************************************************
     * Replace all occurrences of a string within the template content
     *
     * @param from The string to replace
     * @param to   The value to replace with
     */
    public void replaceThis(String from, String to) {
        // don't waste time
        if (!from.isEmpty()) {
            // Infinite loop safety
            to = to.replace(TAG_ALL_VALUES, "{ALL VALUES TAG}"); // all values tag safety
            if (to.lastIndexOf(from) > 0) {
                String message = "Replace Attempted with Replace Value containg Replace Key:" + from + "\n Value:" + to + " in " + getFullName();
                if (isSoftFail()) {
                    log.warn("SOFT FAIL -" + message);
                    to = "SOFT FAIL - VALUE NOT REPLACED, TO CONTAINS FROM";
                } else {
                    throw new RuntimeException(message + "\n" + getStack());
                }
            }
            // do the replace
            int index;
            while ((index = content.lastIndexOf(from)) != -1) {
                content.replace(index, index + from.length(), to);
            }
        }
    }

    /********************************************************************************
     * Replace all using RegEx Pattern
     *
     * @param pattern the Regex Pattern to search for
     * @param to      The string to replace the pattern with
     */
    public void replaceAllThis(Pattern pattern, String to) {
        Matcher m = pattern.matcher(content);
        content.replace(0, content.length(), m.replaceAll(to));
    }

    /********************************************************************************
     * does the replace hash contain a specific key
     *
     * @param key The key of the entry to get
     * @return boolean True if the key exists
     */
    public boolean hasReplaceKey(String key) {
        return replaceValues.containsKey(key);
    }

    /********************************************************************************
     * does the replace hash contain a specific key, with a non-blank value.
     *
     * @param key The key of the entry to get
     * @return boolean True if the key exists with a non-empty value.
     */
    public boolean hasReplaceValue(String key) {
        return replaceValues.containsKey(key) && !replaceValues.get(key).isEmpty();
    }

    /********************************************************************************
     * addReplace Add a from-to pair with a wrapped from value
     *
     * @param from The from value
     * @param to   The replace value
     */
    public void addReplace(String from, String to) {
        replaceValues.put(wrap(from), to);
    }

    /********************************************************************************
     * set a replace value to the empty string ""
     *
     * @param keys The list of keys to empty
     */
    public void addEmptyReplace(List<String> keys) {
        for (String from : keys) {
            replaceValues.put(wrap(from), "");
        }
    }

    public void appendToReplaceValue(String key, String value, String seperator) {
        if (!this.replaceValues.containsKey(key)) {
        	this.replaceValues.put(key, value);
        } else {
        	this.replaceValues.put(key, this.replaceValues.get(key) + seperator + value);
        }
    }

    /********************************************************************************
     * Process the replace hash on the provided string
     *
     * @param value The string to process
     * @return The string after processing the replace string
     */
    public String replaceProcess(String value) {
        for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }
        return value;
    }

    /********************************************************************************
     * get a value from the replace hash
     *
     * @param key The key of the entry to get
     * @return value, the value of that entry.
     * @throws MergeException when value not found.
     * @see hasReplaceKey and hasReplaceValue()
     */
    public String getReplaceValue(String key) {
        if (replaceValues.containsKey(key)) {
            return replaceValues.get(key);
        } else {
            throw new IllegalArgumentException("Unknown key: " + key);
        }
    }
    
    /**
     * @return soft fail indicator (from replace hash)
     */
    public boolean isSoftFail() {
        return replaceValues.containsKey(TAG_SOFTFAIL);
    }

    /**
     * @return the Template Stack (from replace hash)
     */
    public String getStack() {
        return getReplaceValue(TAG_STACK);
    }

    /**
     * @return the Output Hash String (from replace hash)
     */
    public String getOutputHash() {
        return getReplaceValue(TAG_OUTPUTHASH);
    }

    /**
     * @return output type indicator (Default to tar, allow "zip" over-ride
     */
    public String getOutputType() {
        return (replaceValues.containsKey(TAG_OUTPUT_TYPE) && replaceValues.get(TAG_OUTPUT_TYPE).equals(TYPE_ZIP)) ? TYPE_ZIP : TYPE_TAR;
    }

    /********************************************************************************
     * Content is empty (whitespace)
     *
     * @return boolean empty
     */
    public boolean isEmpty() {
        return content.toString().trim().isEmpty();
    }

    /********************************************************************************
     * @return the full name (i.e. collection + Name + Column)
     */
    public String getFullName() {
        return collection + "." + name + "." + (columnValue == null ? "" : columnValue);
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public Map<String, String> getReplaceValues() {
        return replaceValues;
    }

    public long getIdtemplate() {
        return idtemplate;
    }

    public String getCollection() {
        return collection;
    }

    public void addDirective(AbstractDirective newDirective) {
        newDirective.setTemplate(this);
        newDirective.setIdTemplate(idtemplate);
        newDirective.setSequence(directives.size());
        directives.add(newDirective);
    }

    public String getColumnValue() {
        return columnValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
    	if (description.length() > 250) {
    		return description.substring(0, 250);
    	} else {
            return description;
    	}
    }

    /**
     * @return the Output File name (from replace hash)
     */
    public String getOutputFile() {
        return this.outputFile;
    }

    public String getContent() {
        return content.toString();
    }

    public String getOutput() {
        return outputFile;
    }

    public List<AbstractDirective> getDirectives() {
        return directives;
    }

    private void setMerged(boolean b) {
		this.merged = b;
		
	}

    public Boolean getMergable() {
		return mergable;
	}

	public void setBookmarks(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	public void setMergable(Boolean mergable) {
		this.mergable = mergable;
	}

	public void setDirectives(List<AbstractDirective> directives) {
		this.directives = directives;
	}

	public void setReplaceValues(Map<String, String> replaceValues) {
		this.replaceValues = replaceValues;
	}

	public void setContent(String content) {
        setContent(new StringBuilder(content));
    }

    /**
     * setContent - initilize and parse Bookmarks collection from content
     *
     * @param content
     * @throws MergeException
     */
    public void setContent(StringBuilder content) {
        this.content = content;
        // Parse bookmarks array from text
        initializeBookmarksFromContent();
    }

    private void initializeBookmarksFromContent() {
        Matcher m = BOOKMARK_PATTERN.matcher(this.content);
        bookmarks = new ArrayList<>();
        while (m.find()) {
            bookmarks.add(new Bookmark(m.group(), m.start()));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Template template = (Template) o;
        return getFullName().equals(template.getFullName());
    }

    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }

    @Override
    public String toString() {
        return "Template{" +
                "idtemplate=" + idtemplate +
                ", collection='" + collection + '\'' +
                ", columnValue='" + columnValue + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", outputFile='" + outputFile + '\'' +
                '}';
    }
}
