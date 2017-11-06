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
