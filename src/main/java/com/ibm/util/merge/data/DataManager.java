/*
 * 
 * Copyright 2015-2017 IBM
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
package com.ibm.util.merge.data;

import java.util.ArrayList;
import java.util.Map;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * The data manager provides a path based interface to the Data Element structure
 * 
 * @author Mike Storey
 *
 */
public class DataManager {
	private DataObject data = new DataObject();;
	private ArrayList<DataElement> contextStack;
	
	/**
	 * Instantiate a new Data Manager
	 */
	public DataManager() {
		contextStack = new ArrayList<DataElement>();
	}
	
	/**
	 * Test if Data Manager has an element at the provide address
	 * 
	 * @param address
	 * @param delimiter
	 * @return
	 */
	public boolean contians(String address, String delimiter) {
		Path path = new Path(address, delimiter);
		if (address.equals(Merger.IDMU_CONTEXT)) {
			return (this.contextStack.size() != 0);
		}

		try {
			getElement(path,0);
		} catch (MergeException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get a value from the data manager based on the provided address
	 * 
	 * @param address
	 * @param delimiter
	 * @return
	 * @throws MergeException
	 */
	public DataElement get(String address, String delimiter) throws MergeException {
		if (address.equals(Merger.IDMU_CONTEXT)) {
			if (contextStack.isEmpty()) {
				throw new Merge500("Context EMPTY!");
			}
			return this.contextStack.get(contextStack.size()-1);
		}
		Path path = new Path(address, delimiter);
		DataElement element = getElement(path,0);
		return element;
	}
	
	/**
	 * Add a value to the Data Manager. Note that the data manager provides a loss-less
	 * approach to adding values. If the value already exists as a non-list element it 
	 * will converted to a list and this value added to that list. 
	 * 
	 * @param address
	 * @param delimiter
	 * @param value
	 * @throws MergeException
	 */
	public void put(String address, String delimiter, DataElement value) throws MergeException {
		Path path = new Path(address, delimiter);
		DataElement entry;
			entry = getElement(path, 1);
			PathPart part = path.pop();
			if (entry.isObject()) {
				entry.getAsObject().put(part.part, value);
			} else if (entry.isList()) {
				entry.getAsList().add(part.index, value);
			} else {
				entry = entry.makeArray();
				entry.getAsList().add(value);
			}
	}
	
	/**
	 * Convenience method to add the HTTP Request Parameters to the data manager
	 * 
	 * @param address
	 * @param delimiter
	 * @param parameterMap
	 * @throws MergeException
	 */
	public void put(String address, String delimiter, Map<String, String[]> parameterMap) throws MergeException {
		DataObject parameters = new DataObject();
		for (String parameter : parameterMap.keySet()) {
			DataList values = new DataList(); 
			for (String value : parameterMap.get(parameter)) {
				values.add(new DataPrimitive(value));
			}
			parameters.put(parameter, values);
		}
		this.put(address, delimiter, parameters);
	}

	/**
	 * Convenience method to add a primitive value
	 * 
	 * @param address
	 * @param delimiter
	 * @param value
	 * @throws MergeException
	 */
	public void put(String address, String delimiter, String value) throws MergeException {
		DataPrimitive primitiveValue = new DataPrimitive(value);
		this.put(address, delimiter, primitiveValue);
	}

	/**
	 * Convenience method to add a value with the default path separator
	 * 
	 * @param path
	 * @param value
	 * @throws MergeException
	 */
	public void put(Path path, DataElement value) throws MergeException {
		put(path.getPath(), path.getSeparator(), value);
	}
	
	/**
	 * Get a value from the data manger from the provided address.
	 * 
	 * @param path
	 * @param to
	 * @return
	 * @throws MergeException
	 */
	private DataElement getElement(Path path, int to) throws MergeException {
		DataElement entry = null;
		if (path.get(0).part.equals(Merger.IDMU_CONTEXT)) {
			entry = this.contextStack.get(this.contextStackSize()-1);
			path.remove(0);
		} else {
			entry = this.data;
		}
	
		while (path.size() > to) {
			PathPart part = path.pop();
			if (part.isList) {
				if (!entry.isList()) throw new Merge500("Array not found at " + path.getCurrent() + " in " + path.getPath());
				if (entry.getAsList().size()-1 < part.index) throw new Merge500("Array to small at:" + path.getCurrent() + " in " + path.getPath());
				entry = entry.getAsList().get(part.index);
			} else {
				if (!entry.isObject()) throw new Merge500("Object not found at " + path.getCurrent() + " in " + path.getPath());
				if (!entry.getAsObject().containsKey(part.part)) throw new Merge500("Object does not have attribute:" + part.part + " at:" + path.getCurrent() + " in " + path.getPath());
				entry = entry.getAsObject().get(part.part);
			}
		}
		return entry;
	}

	// Simple getters and setters below here
	
	/**
	 * @return size of base entry set
	 */
	public int size() {
		return data.entrySet().size();
	}
	
	/**
	 * Clear the data manager
	 */
	public void clear() {
		this.data = new DataObject();
	}
	
	/**
	 * @param context - the context address to add to the stack
	 */
	public void pushContext(DataElement context) {
		contextStack.add(context);
	}
	
	/**
	 * remove a context from the stack
	 */
	public void popContext() {
		contextStack.remove(contextStack.size()-1);
	}
	
	/**
	 * @return context stack size
	 */
	public int contextStackSize() {
		return contextStack.size();
	}
	
}

