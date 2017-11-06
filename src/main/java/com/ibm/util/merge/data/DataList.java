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

import com.ibm.util.merge.exception.Merge500;

/**
 * The DataList object represents a list of values in the DataManager
 * 
 * @author Mike Storey
 * @since v4.0.0
 */
public class DataList extends ArrayList<DataElement> implements DataElement {
	private transient DataElement parent = null;
	private transient String name;
	private transient int position;
	
	/**
	 * extends ArrayList 
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * Instantiate an empty list object
	 */
	public DataList() {
		super();
	}

	@Override
	public boolean add(DataElement newElement) {
		newElement.setParent(this);
		newElement.setPosition(this.size());
		return super.add(newElement);
	}

	@Override
	public DataElement set(int position, DataElement newElement) {
		newElement.setParent(this);
		newElement.setPosition(position);
		return super.set(position, newElement);
	}
	
	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public boolean isList() {
		return true;
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
	public DataList getAsList() {
		return this;
	}

	@Override
	public DataObject getAsObject() throws Merge500 {
		throw new Merge500("Not a Object");
	}

	@Override
	public DataElement makeArray() throws Merge500 {
		return this;
	}

	@Override
	public DataElement getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public void setParent(DataElement parent) {
		this.parent = parent;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}

}
