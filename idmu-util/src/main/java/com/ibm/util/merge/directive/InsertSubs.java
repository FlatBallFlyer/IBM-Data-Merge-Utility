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

import com.ibm.util.merge.template.Bookmark;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.directive.provider.DataTable;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Insert Subs directive drives the insertion of sub-templates at bookmarks
 * for each row of data in the DataTables provided by the Data Provider
 *
 * @author flatballflyer
 */
public abstract class InsertSubs extends AbstractDirective {
    private static final Logger log = Logger.getLogger(InsertSubs.class.getName());
    private static final int DEPTH_MAX = 100;
    private List<String> notLast = new ArrayList<>();
    private List<String> onlyLast = new ArrayList<>();

    /**
     * Simple constructor
     */
    public InsertSubs() {
        super();
    }

    public InsertSubs(InsertSubs from) {
    	super(from);
    	this.setNotLast( 	from.getNotLast());
    	this.setOnlyLast( 	from.getOnlyLast());
    }
    
    /**
     * Execute Directive - will get the data and insert the sub-templates
     *
     * @param target Template to insert sub-templates into
     * @param tf
     * @param rtc
     * @throws MergeException        Infinite Loop Safety, or subTemplate Create/Merge
     * @throws DragonFlySqlException SQL Error thrown in Template.new
     */
    @Override
    public void executeDirective(MergeContext rtc) throws MergeException {
        log.info("Inserting Subtemplates into: " + getTemplate().getFullName());
        // Depth counter - infinite loop safety mechanism
        if (getTemplate().getStack().split("/").length >= DEPTH_MAX) {
            throw new MergeException("Insert Subs Infinite Loop suspected", getTemplate().getStack());
        }
        // Get the table data and iterate the rows
        getProvider().getData(rtc);
        for (DataTable table : getProvider().getTables()) {

            for (int row = 0; row < table.size(); row++) {
                log.info("Inserting Record #" + row + " into: " + getTemplate().getFullName());
                // Iterate over target bookmarks
                for (Bookmark bookmark : getTemplate().getBookmarks()) {
                    log.info("Processing bookmark " + bookmark.getName());
                    // Create and Insert a new sub-template
                    processSubTemplate(rtc, table, row, bookmark);
                }
            }
        }
    }

    private void processSubTemplate(MergeContext rtc, DataTable table, int row, Bookmark bookmark) throws MergeException {
        String collection = bookmark.getCollection();
        String name = bookmark.getName();
        String column = table.getValue(row, bookmark.getColumn());
        Template subTemplate = rtc.getTemplateFactory().getMergableTemplate(collection + "." + name + "." + column, collection + "." + name + ".", getTemplate().getReplaceValues());
        log.info("Inserting Template " + subTemplate.getFullName() + " into " + getTemplate().getFullName());
        // Add the Row replace values
        for (int col = 0; col < table.cols(); col++) {
            subTemplate.addReplace(table.getCol(col), table.getValue(row, col));
        }
        // Take care of "Not Last" and "Only Last"
        subTemplate.addEmptyReplace(row == table.size() - 1 ? notLast : onlyLast);
        // Merge the SubTemplate and insert the text into the Target Template
        try {
        	final String returnValue = subTemplate.getMergedOutput(rtc);
            getTemplate().insertText(returnValue, bookmark);
        } catch (MergeException e) {
            if (isSoftFail() || isSoftFailTemplate()) {
                log.warn("Soft Fail on Insert");
                getTemplate().insertText("Soft Fail Exception" + e.getMessage(), bookmark);
            } else {
                throw e;
            }
        }
    }
    
    public String getNotLast() {
        return String.join(",", notLast);
    }

    public void setNotLast(String notLast) {
        this.notLast = new ArrayList<>(Arrays.asList(notLast.split(",")));
    }

    public String getOnlyLast() {
        return String.join(",", onlyLast);
    }

    public void setOnlyLast(String onlyLast) {
        this.onlyLast = new ArrayList<>(Arrays.asList(onlyLast.split(",")));
    }
}
