package com.ibm.util.merge.data;

import java.util.ArrayList;

import com.ibm.util.merge.exception.Merge500;

public class DataList extends ArrayList<DataElement> implements DataElement {
	private transient DataElement parent = null;
	private transient String name;
	private transient int position;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

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
