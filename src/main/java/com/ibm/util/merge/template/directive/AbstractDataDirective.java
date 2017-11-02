package com.ibm.util.merge.template.directive;

import java.util.HashMap;


public abstract class AbstractDataDirective extends AbstractDirective {
	protected String dataSource;
	protected String dataDelimeter;
	protected int ifMissing;
	protected int ifPrimitive;
	protected int ifObject;
	protected int ifList;

	public abstract HashMap<Integer, String> missingOptions();
	public abstract HashMap<Integer, String> primitiveOptions();
	public abstract HashMap<Integer, String> objectOptions();
	public abstract HashMap<Integer, String> listOptions();
	
	public AbstractDataDirective() {
		super();
		this.dataSource = "";
		this.dataDelimeter = "";
	}
	
	public AbstractDataDirective(String source, String delimeter, int missing, int primitive, int object, int list) {
		super();
		this.dataSource = source;
		this.dataDelimeter = delimeter;
		this.ifMissing = missing;
		this.ifPrimitive = primitive;
		this.ifObject = object;
		this.ifList = list;
	}
	
	public void makeMergable(AbstractDataDirective mergable) {
		mergable.setType(this.getType());
		mergable.setName(name);
		mergable.setDataSource(this.getDataSource());
		mergable.setDataDelimeter(this.getDataDelimeter());
		mergable.setIfSourceMissing(this.getIfSourceMissing());
		mergable.setIfList(this.getIfList());
		mergable.setIfObject(this.getIfObject());
		mergable.setIfPrimitive(this.getIfPrimitive());
	}

	public String getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public void setIfSourceMissing(int value) {
		if (this.missingOptions().keySet().contains(value)) {
			this.ifMissing = value;
		}
	}

	public int getIfSourceMissing() {
		return this.ifMissing;
	}

	public void setIfPrimitive(int value) {
		if (this.primitiveOptions().containsKey(value)) {
			this.ifPrimitive = value;
		}
	}

	public int getIfPrimitive() {
		return this.ifPrimitive;
	}

	public void setIfObject(int value) {
		if (this.objectOptions().containsKey(value)) {
			this.ifObject = value;
		}
	}

	public int getIfObject() {
		return this.ifObject;
	}

	public void setIfList(int value) {
		if (this.listOptions().containsKey(value)) {
			this.ifList = value;
		}
	}

	public int getIfList() {
		return this.ifList;
	}

	public String getDataDelimeter() {
		return this.dataDelimeter;
	}
	public void setDataDelimeter(String dataDelimeter) {
		this.dataDelimeter = dataDelimeter;
		
	}

}
