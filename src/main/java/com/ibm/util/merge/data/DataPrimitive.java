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

import com.ibm.util.merge.exception.Merge500;

/**
 * Represents a primitive (String) Data Element
 * 
 * @author Mike Storey
 *
 */
public class DataPrimitive implements DataElement {
	private transient DataElement parent = null;
	private transient String name = null;
	private transient int position = 0;
	private final String value;
	
	/**
	 * Construct a new primitive object with Value
	 * 
	 * @param value The string to initialize with
	 */
	public DataPrimitive(String value) {
		super();
		if (value == null) {
			this.value = ""; 
		} else {
			this.value = value;
		}
	}
	
	/**
	 * Construct a new primitive object with toString(Value)
	 * 
	 * @param value The value to initialize with
	 */
	public DataPrimitive(int value) {
		super();
		this.value = Integer.toString(value);
	}
	
	/**
	 * Construct a new primitive object with toString(Value)
	 * 
	 * @param value The value to assign
	 */
	public DataPrimitive(double value) {
		super();
		this.value = Double.toString(value);
	}
	
	/**
	 * @return the value
	 */
	public String get() {
		return value;
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

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public boolean isList() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	public String getAsPrimitive() throws Merge500 {
		return value;
	}

	@Override
	public DataList getAsList() throws Merge500 {
		throw new Merge500("Not a List");
	}

	@Override
	public DataObject getAsObject() throws Merge500 {
		throw new Merge500("Not a Object");
	}

	@Override
	public DataElement getParent() {
		return parent;
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
