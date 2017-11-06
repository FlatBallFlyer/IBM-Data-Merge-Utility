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
 * A DataElement is the abstract base for the IDMU Data Object structure.
 * 
 * @author Mike Storey
 *
 */
public abstract interface DataElement {
	DataElement parent = null;
	String name = null;
	int position = 0;

	public abstract boolean isObject();
	public abstract boolean isList();
	public abstract boolean isPrimitive();
	public abstract String getAsPrimitive() throws Merge500;
	public abstract DataList getAsList() throws Merge500;
	public abstract DataObject getAsObject() throws Merge500;
	public abstract DataElement makeArray() throws Merge500;
	public abstract DataElement getParent() throws Merge500;
	public abstract void setParent(DataElement parent);
	public abstract String getName();
	public abstract void setName(String name);
	public abstract int getPosition();
	public abstract void setPosition(int position);
}
