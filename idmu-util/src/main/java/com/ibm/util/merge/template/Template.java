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

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.directive.AbstractDirective;
import org.apache.log4j.Logger;

import java.util.*;
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
public class Template implements Cloneable {
    // Global Constants (Reserved Tags)
    public static final String LFT = "{";
    public static final String RGT = "}";
    public static final String TAG_STACK = wrap("DragonFlyTemplateStack");
    public static final String TAG_ALL_VALUES = wrap("DragonFlyReplaceValues");
    public static final String TAG_OUTPUTFILE = wrap("DragonFlyOutputFile");
    public static final String TAG_OUTPUTHASH = wrap("DragonFlyOutputHash");
    public static final String TAG_SOFTFAIL = wrap("DragonFlySoftFail");
    public static final String TAG_OUTPUT_TYPE = wrap("DragonOutputType");
    public static final String TAG_SEQUENCE = wrap("DragonSequence");
    public static final String BOOKMARK_PATTERN_STRING = "(<tkBookmark.*?/>)";
    public static final Pattern BOOKMARK_PATTERN = Pattern.compile(BOOKMARK_PATTERN_STRING);
    // Factory Constants
    public static final int TYPE_ZIP = 1;
    public static final int TYPE_TAR = 2;
    // Template Constants
    private static final Logger log = Logger.getLogger(Template.class.getName());
    // Attributes
    private transient long idtemplate = 0;
    private String collection = "";
    private String columnValue = "";
    private String name = "";
    private String description = "";
    private String outputFile = "";
    private StringBuilder content = new StringBuilder();
    private List<AbstractDirective> directives = new ArrayList<>();
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
     * Template Clone constructor, used to get a copy of a cached template and initialize it
     * for merge processing. Performs deep copy of all objects and collections and populates
     * the replace values hash with initial values.
     *
     * @param seedReplace Initial replace hash
     * @throws MergeException - Wrapper of clone not supported exceptions
     */
    public Template clone(Map<String, String> seedReplace) {
        Template newTemplate = cloneThisTemplate();
        newTemplate.replaceValues = new HashMap<>();
        newTemplate.setContent(getContent());
        // Deep Copy Directives
        newTemplate.directives = new ArrayList<>();
        for (AbstractDirective fromDirective : directives) {
            AbstractDirective clone = cloneDirective(fromDirective);
            newTemplate.addDirective(clone);
        }
        // Seed Replace Values
        newTemplate.replaceValues.putAll(seedReplace);
        // Make sure we have an output file name guid
        if (!isOutputFileSpecified()) {
            newTemplate.replaceValues.put(TAG_OUTPUTFILE, UUID.randomUUID().toString() + (getOutputType() == TYPE_ZIP ? ".zip" : ".tar"));
        }
        // Add our name to the Template Stack
        if (!newTemplate.replaceValues.containsKey(TAG_STACK)) {
            newTemplate.replaceValues.put(TAG_STACK, getFullName());
        } else {
            newTemplate.replaceValues.put(TAG_STACK, newTemplate.replaceValues.get(TAG_STACK) + "/" + newTemplate.getFullName());
        }
        return newTemplate;
    }

    private Template cloneThisTemplate() {
        Template newTemplate;
        try {
            newTemplate = (Template) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Template implementation does not support clone()", e);
        }
        return newTemplate;
    }

    private AbstractDirective cloneDirective(AbstractDirective fromDirective) {
        AbstractDirective clone;
        try {
            clone = fromDirective.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Template implementation does not support clone()", e);
        }
        return clone;
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
    public void merge(RuntimeContext rtc) throws MergeException {
        log.info("Begin Template Merge for:" + getFullName());
        // Process Directives
        for (AbstractDirective directive : directives) {
            directive.executeDirective(rtc);
        }
        // Clear out the all-values replace tag
        replaceValues.remove(TAG_ALL_VALUES);
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
        log.info("Merge Complete: " + getFullName());
        rtc.getConnectionFactory().releaseConnection(getOutputFile());
        log.info("ReleasedConnection for " + getOutputFile());
    }

    public boolean isOutputFileSpecified() {
        boolean specified = replaceValues.containsKey(TAG_OUTPUTFILE);
        if (!specified) {
            log.warn("Template " + getFullName() + " is missing System Tag : " + Template.TAG_OUTPUTFILE);
        }
        if (specified && getOutputFile().isEmpty()) {
            log.warn("Output File is empty for template " + getFullName());
            specified = false;
        }
        return specified;
    }

    public boolean isNullOutputFile() {
        return outputFile.equals("/dev/null");
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
     * @return the Output File name (from replace hash)
     */
    public String getOutputFile() {
        return getReplaceValue(TAG_OUTPUTFILE);
    }

    /**
     * @return the Output Hash String (from replace hash)
     */
    public String getOutputHash() {
        return getReplaceValue(TAG_OUTPUTHASH);
    }

    /**
     * @return output type indicator (Default to GZIP, allow "zip" over-ride
     */
    public int getOutputType() {
        return (replaceValues.containsKey(TAG_OUTPUT_TYPE) && replaceValues.get(TAG_OUTPUT_TYPE).endsWith("zip")) ? TYPE_ZIP : TYPE_TAR;
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
        return collection + "." + name + "." + columnValue;
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
        return description;
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

    public boolean canWrite() {
        boolean missingWriteProperties = isEmpty() || isNullOutputFile() || !isOutputFileSpecified();
        return !missingWriteProperties;
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
