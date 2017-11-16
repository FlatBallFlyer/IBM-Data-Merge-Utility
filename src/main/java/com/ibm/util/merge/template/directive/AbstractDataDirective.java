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

import com.ibm.util.merge.Config;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Content;


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
	 * @param source The Source name
	 * @param delimeter The source delimiter
	 * @param missing The ifMissing option
	 * @param hasTags Indicates the Source Name has Replace Tags in it
	 * @param primitive The ifPrimitive option
	 * @param object The ifObject option
	 * @param list The ifList option
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
	 * Populate Transient Values and validate enumerations
	 * @param template the Template to connect to
	 * @throws MergeException on failed validations
	 */
	public void cachePrepare(Template template) throws MergeException {
		super.cachePrepare(template);
		// TODO Validate Enums
	}

	/**
	 * Create a mergable clone of the object
	 * 
	 * @param mergable The directive to make mergable
	 */
	public void makeMergable(AbstractDataDirective mergable) {
		mergable.setType(this.getType());
		mergable.setName(this.getName());
		mergable.setDataSource(this.getRawDataSource());
		mergable.setDataDelimeter(this.getDataDelimeter());
		mergable.setSourceHasTags(this.getSourceHasTags());
		mergable.setIfSourceMissing(this.getIfSourceMissing());
		mergable.setIfList(this.getIfList());
		mergable.setIfObject(this.getIfObject());
		mergable.setIfPrimitive(this.getIfPrimitive());
	}

	/**
	 * @return data source name
	 * @throws MergeException when source not found
	 */
	public String getDataSource() throws MergeException {
		String source = this.dataSource;
		if (this.sourceHasTags) {
			Content content = new Content(this.getTemplate().getWrapper(), source, this.getTemplate().getContentEncoding());;
			content.replace(this.getTemplate().getReplaceStack(), true, Config.nestLimit());
			source = content.getValue();
		}
		return source;
	}
	
	/**
	 * @return data source name
	 */
	public String getRawDataSource() {
		return dataSource;
	}
	
	/*
	 * Simple Setter/Getter code below here
	 */
	/**
	 * @param dataSource The data source name
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @param value Source is Missing
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
	 * @param value New if Primitive option
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
	 * @param value New if Object option
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
	 * @param value New if List option
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
	 * @return the Delimiter
	 */
	public String getDataDelimeter() {
		return this.dataDelimeter;
	}
	/**
	 * @param dataDelimeter The data delimiter
	 */
	public void setDataDelimeter(String dataDelimeter) {
		this.dataDelimeter = dataDelimeter;
		
	}

	/**
	 * @return the Has Tags inidcator
	 */
	public boolean getSourceHasTags() {
		return sourceHasTags;
	}

	/**
	 * @param sourceHasTags The has tags indicator
	 */
	public void setSourceHasTags(boolean sourceHasTags) {
		this.sourceHasTags = sourceHasTags;
	}

}
