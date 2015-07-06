/*
 * Copyright 2015, 2015 IBM
 * 
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

package com.ibm.util.merge.directive;

import com.ibm.util.merge.*;
import com.ibm.util.merge.directive.provider.Provider;

/**
 * A merge directive which drive the merge process for a template 
 *
 * @author  Mike Storey
 */
public abstract class Directive implements Cloneable{
	// Directive Types
	public static final int TYPE_REQUIRE 				= 0;
	public static final int TYPE_REPLACE_VALUE 			= 1;
	public static final int TYPE_TAG_INSERT	 			= 2;
	public static final int	TYPE_SQL_INSERT				= 10;
	public static final int TYPE_SQL_REPLACE_ROW 		= 11;
	public static final int TYPE_SQL_REPLACE_COL 		= 12;
	public static final int	TYPE_CSV_INSERT				= 21;
	public static final int TYPE_CSV_REPLACE_ROW 		= 22;
	public static final int TYPE_CSV_REPLACE_COL 		= 23;
	public static final int	TYPE_HTML_INSERT			= 31;
	public static final int TYPE_HTML_REPLACE_ROW 		= 32;
	public static final int TYPE_HTML_REPLACE_COL 		= 33;
	public static final int TYPE_HTML_REPLACE_MARKUP 	= 34;
	// Planned directive Types (Not Implemented)
	public static final int	TYPE_JSON_INSERT			= 41;
	public static final int TYPE_JSON_REPLACE_ROW 		= 42;
	public static final int TYPE_JSON_REPLACE_COL 		= 43;
	public static final int	TYPE_XML_INSERT				= 51;
	public static final int TYPE_XML_REPLACE_ROW 		= 52;
	public static final int TYPE_XML_REPLACE_COL 		= 53;
	public static final int	TYPE_MONGO_INSERT			= 61;
	public static final int TYPE_MONGO_REPLACE_ROW 		= 62;
	public static final int TYPE_MONGO_REPLACE_COL 		= 63;
	
	// Attributes
	private transient Template 	template;
	private transient long 		idTemplate 	= 0;
	private int			sequence	= 0;
	private int 		type		= 0;
	private boolean 	softFail	= false;
	private String 		description	= getClass().getName();
	private Provider 	provider;
	
	/********************************************************************************
	 * Simple Constructor
	 */
	public Directive() {
	}
	
	/********************************************************************************
	 * Abstract method to "Execute" the directive in the context of a template.
	 *
	 * @throws MergeException execution errors
	 * @param tf
	 * @param rtc
	 */
	public abstract void executeDirective(RuntimeContext rtc) throws MergeException;

	/********************************************************************************
	 * Cone constructor
	 * @throws CloneNotSupportedException
	 */
	public Directive clone() throws CloneNotSupportedException {
		Directive newDirective = (Directive) super.clone();
		if (provider != null) {
			newDirective.setProvider( (Provider) provider.clone() );
		}
		newDirective.template = null;
		return newDirective;
	}
	
	/********************************************************************************
	 * SoftFail indicator (on this Directive, or the Template)
	 */
	public boolean softFail() {
		return (softFail | template.isSoftFail()) ? true : false;
	}
	
	/**
	 * @return the Template Fullname + the Directive Description
	 */
	public String getFullName() {
		return template.getFullName() + ":Directive-" + description;
	}
	
	public Template getTemplate() {
		return template;
	}
	
	public Provider getProvider() {
		return provider;
	}

	public boolean isSoftFail() {
		return softFail;
	}

	public int getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}
	public long getIdTemplate() {
		return idTemplate;
	}

	public void setIdTemplate(long idTemplate) {
		this.idTemplate = idTemplate;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
		if (provider != null) {
			provider.setDirective(this);
		}
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setSoftFail(boolean softFail) {
		this.softFail = softFail;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}	
}
