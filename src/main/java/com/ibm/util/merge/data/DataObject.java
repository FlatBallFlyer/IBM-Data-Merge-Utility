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

import java.util.HashMap;

import com.ibm.util.merge.exception.Merge500;

/**
 * Represents an Object (Map of unique String to Value pairs)
 * 
 * @author Mike Storey
 *
 */
public class DataObject extends HashMap<String,DataElement> implements DataElement {
	private transient DataElement parent = null;
	private transient String name;
	private transient int position;

	/**
	 * extends HashMap<String, DataElement> 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates an empty Object data element
	 */
	public DataObject() {
		super();
	}


	@Override
	public DataElement put(String name, DataElement newElement) {
		newElement.setName(name);
		newElement.setParent(this);
		return super.put(name, newElement);
	}
	
	@Override
	public DataElement makeArray() throws Merge500 {
		if (null == parent) throw new Merge500("No Parent!"); 
		DataList newList = null;
		if (parent.isList()) {
			newList = new DataList();
			parent.getAsList().set(this.position, newList);
			newList.add(this);
		}
		if (parent.isObject()) {
			newList = new DataList();
			parent.getAsObject().put(this.name, newList);
			newList.add(this);
		}
		return newList;
	}

	// Simple getter / setter methods below here
	
	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public boolean isList() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public String getAsPrimitive() throws Merge500 {
		throw new Merge500("Not a Primitive");
	}

	@Override
	public DataList getAsList() throws Merge500 {
		throw new Merge500("Not a Lilst");
	}

	@Override
	public DataObject getAsObject() throws Merge500 {
		return this;
	}

	@Override
	public DataElement getParent() {
		return this.parent;
	}

	@Override
	public void setParent(DataElement parent) {
		this.parent = parent;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}

}
