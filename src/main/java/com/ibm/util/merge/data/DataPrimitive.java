package com.ibm.util.merge.data;

import com.ibm.util.merge.exception.Merge500;

public class DataPrimitive implements DataElement {
	private transient DataElement parent = null;
	private transient String name = null;
	private transient int position = 0;
	private String value;
	
	public DataPrimitive(String value) {
		super();
		if (value == null) {
			this.value = ""; 
		} else {
			this.value = value;
		}
	}
	
	public DataPrimitive(int value) {
		super();
		this.value = Integer.toString(value);
	}
	
	public DataPrimitive(double value) {
		super();
		this.value = Double.toString(value);
	}
	
	public String get() {
		return value;
	}

	public void set(String value) {
		this.value = value;
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
