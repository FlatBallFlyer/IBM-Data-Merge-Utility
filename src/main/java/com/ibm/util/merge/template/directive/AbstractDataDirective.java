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
package com.ibm.util.merge.template.directive;


/**
 * Abstract Directive that provides a set of "Data Source" attributes
 *  
 * @author Mike Storey
 *
 */
public abstract class AbstractDataDirective extends AbstractDirective {
	private String dataSource;
	private String dataDelimeter;
	private boolean sourceHasTags;
	private int ifMissing;
	private int ifPrimitive;
	private int ifObject;
	private int ifList;

	/**
	 * Instantiate a Data Directive with Defaults
	 */
	public AbstractDataDirective() {
		super();
		this.dataSource = "";
		this.dataDelimeter = "";
		this.setSourceHasTags(false);
	}
	
	/**
	 * Instantiate a Data Directive with provided values
	 * @param source
	 * @param delimeter
	 * @param missing
	 * @param primitive
	 * @param object
	 * @param list
	 */
	public AbstractDataDirective(String source, String delimeter, boolean hasTags, int missing, int primitive, int object, int list) {
		super();
		this.dataSource = source;
		this.dataDelimeter = delimeter;
		this.sourceHasTags = hasTags;
		this.ifMissing = missing;
		this.ifPrimitive = primitive;
		this.ifObject = object;
		this.ifList = list;
	}
	
	/**
	 * Create a mergable clone of the object
	 * 
	 * @param mergable
	 */
	public void makeMergable(AbstractDataDirective mergable) {
		mergable.setType(this.getType());
		mergable.setName(this.getName());
		mergable.setDataSource(this.getDataSource());
		mergable.setDataDelimeter(this.getDataDelimeter());
		mergable.setSourceHasTags(this.getSourceHasTags());
		mergable.setIfSourceMissing(this.getIfSourceMissing());
		mergable.setIfList(this.getIfList());
		mergable.setIfObject(this.getIfObject());
		mergable.setIfPrimitive(this.getIfPrimitive());
	}

	/**
	 * @return data source name
	 */
	public String getRawDataSource() {
		return dataSource;
	}
	
	/**
	 * @return data source name
	 */
	public String getDataSource() {
		return dataSource;
	}
	
	/**
	 * @param dataSource
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @param value
	 */
	public void setIfSourceMissing(int value) {
		this.ifMissing = value;
	}

	/**
	 * @return Source Missing action
	 */
	public int getIfSourceMissing() {
		return this.ifMissing;
	}

	/**
	 * @param value
	 */
	public void setIfPrimitive(int value) {
		this.ifPrimitive = value;
	}

	/**
	 * @return Primitive Process Indicator
	 */
	public int getIfPrimitive() {
		return this.ifPrimitive;
	}

	/**
	 * @param value
	 */
	public void setIfObject(int value) {
		this.ifObject = value;
	}

	/**
	 * @return Object Process Indicator
	 */
	public int getIfObject() {
		return this.ifObject;
	}

	/**
	 * @param value
	 */
	public void setIfList(int value) {
		this.ifList = value;
	}

	/**
	 * @return List Process Indicator
	 */
	public int getIfList() {
		return this.ifList;
	}

	/**
	 * @return
	 */
	public String getDataDelimeter() {
		return this.dataDelimeter;
	}
	/**
	 * @param dataDelimeter
	 */
	public void setDataDelimeter(String dataDelimeter) {
		this.dataDelimeter = dataDelimeter;
		
	}

	public boolean getSourceHasTags() {
		return sourceHasTags;
	}

	public void setSourceHasTags(boolean sourceHasTags) {
		this.sourceHasTags = sourceHasTags;
	}

}
