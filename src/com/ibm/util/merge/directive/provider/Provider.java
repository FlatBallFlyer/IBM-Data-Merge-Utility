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

import java.util.ArrayList;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.Directive;

/**
 * A data provider for standard merge processes. The data provider normalized the view of 
 * tabular data to a collection of DataTable objects, which are simple Array of Array<String> structures
 * 
 * @author Mike Storey
 */
public abstract class Provider implements Cloneable {
	protected ArrayList<DataTable> dataTables;
	protected Directive directive = null;

	/**
	 * Simple constructor
	 * @param newOwner - the Directive that owns this provider.
	 */
	public Provider(Directive newOwner) {
		this.directive = newOwner;
		this.reset();
	}
	
	/**
	 * @param newOwner - The new Directive this is being cloned for
	 * @return the cloned Provider
	 * @throws CloneNotSupportedException
	 */
	public Provider clone(Directive newOwner) throws CloneNotSupportedException {
		Provider newProvider = (Provider) super.clone();
		this.directive = newOwner;
		return newProvider;
	}
	
	/**
	 * @return A new DataTable that has been added to an EMPTY collection.
	 */
	public DataTable reset() {
		this.dataTables = new ArrayList<DataTable>();
		return this.getNewTable();
	}
	
	/**
	 * @param i
	 * @return The specified DataTable
	 */
	public DataTable getTable(int i) {
		return dataTables.get(i);
	}
	/**
	 * @return The DataTables ArrayList
	 */
	public ArrayList<DataTable> getTables() {
		return this.dataTables;
	}
	
	/**
	 * @return The number of Data Tables 
	 */
	public int size() {
		return this.dataTables.size();
	}
	/**
	 * @return a new DataTable object that has been added to the Provider Tables collection
	 */
	public DataTable getNewTable() {
		DataTable newTable = new DataTable();
		this.dataTables.add(newTable);
		return newTable;
	}

	/**
	 * This is the method called to request data from the provider. 
	 * Each provider will fetch data and populate the Provider DataTables structures
	 * 
	 * @throws MergeException
	 */
	public abstract void getData() throws MergeException;
	
	/**
	 * Each provider should implement this method to provide Context about the query
	 * to be used in logging and exception handling.
	 * @return
	 */
	public abstract String getQueryString();
	
}
