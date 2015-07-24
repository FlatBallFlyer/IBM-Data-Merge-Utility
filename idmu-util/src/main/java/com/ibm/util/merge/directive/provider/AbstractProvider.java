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
package com.ibm.util.merge.directive.provider;

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.AbstractDirective;

import java.util.ArrayList;

/**
 * A data provider for standard merge processes. The data provider normalized the view of
 * tabular data to a collection of DataTable objects, which are simple Array of Array<String> structures
 *
 * @author Mike Storey
 */
public abstract class AbstractProvider {
    private transient ArrayList<DataTable> dataTables = new ArrayList<>();
    private transient AbstractDirective directive = null;
    private int type;

    /**
     * Simple constructor
     *
     * @param
     */
    public AbstractProvider() {
        super();
    }

    public abstract AbstractProvider asNew();
    
    public void copyFieldsFrom(AbstractProvider from) {
    	this.dataTables = new ArrayList<DataTable>();
    	this.setType(from.getType());
    	this.directive = null;
    }

    /**
     * @return a new DataTable object that has been added to the Provider Tables collection
     */
    public DataTable addNewTable() {
        DataTable newTable = new DataTable();
        dataTables.add(newTable);
        return newTable;
    }

    /**
     * @return A new DataTable that has been added to an EMPTY collection.
     */
    public void reset() {
        dataTables = new ArrayList<>();
    }

    /**
     * @param i
     * @return The specified DataTable
     */
    public DataTable getTable(int i) {
        if (i < 0 || i >= dataTables.size()) {
            return null;
        }
        return dataTables.get(i);
    }

    /**
     * This is the method called to request data from the provider.
     * Each provider will fetch data and populate the Provider DataTables structures
     *
     * @param cf
     * @throws MergeException
     */
    public abstract void getData(MergeContext rtc) throws MergeException;
    
    /**
     * Each provider should implement this method to provide Context about the query
     * to be used in logging and exception handling.
     *
     * @return
     */
    public abstract String getQueryString();

    /**
     * @return The DataTables ArrayList
     */
    public ArrayList<DataTable> getTables() {
        return dataTables;
    }

    /**
     * @return The number of Data Tables
     */
    public int size() {
        return dataTables.size();
    }

    public AbstractDirective getDirective() {
        return directive;
    }

    public void setDirective(AbstractDirective directive) {
        this.directive = directive;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
