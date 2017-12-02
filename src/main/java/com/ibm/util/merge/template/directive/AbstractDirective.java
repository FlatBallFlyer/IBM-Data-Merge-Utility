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

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

/**
 * <p>Base Directive class for all Directives. Provides simple type/name functions and requires execute implementation</p>
 * 
 * @author Mike Storey
 * @since: v4.0
 * @see com.ibm.util.merge.template.directive.Enrich
 * @see com.ibm.util.merge.template.directive.Replace
 * @see com.ibm.util.merge.template.directive.Insert
 * @see com.ibm.util.merge.template.directive.ParseData
 * @see com.ibm.util.merge.template.directive.SaveFile
 * 
 */
public abstract class AbstractDirective {

	private int type;
	private String name = "";

	private transient Template template;
	private transient Merger context;
	private transient int state = Template.STATE_RAW;

	/**
	 * Instantiate a Directive 
	 */
	public AbstractDirective() {
		this.template = null;
	}
	
	/**
	 * Populate Transient Values and validate enumerations
	 * @param template The template to bind to
	 * @throws MergeException when enumirators fail to validate
	 */
	public void cachePrepare(Template template) throws MergeException {
		this.template = template;
		this.state = Template.STATE_CACHED;
	}

	/**
	 * Each directive must implement a clone-like get mergable
	 * @param context The merge context to be used
	 * @return the Mergable directive
	 * @throws MergeException on processing errors
	 */
	public abstract AbstractDirective getMergable(Merger context) throws MergeException;

	/**
	 * Get a mergable copy of this directive
	 * @param context The merge context to be used
	 * @param target The directive to make mergable
	 */
	public void makeMergable(AbstractDirective target, Merger context) {
		target.setType(this.getType());
		target.setName(name);
		target.state = Template.STATE_MERGABLE;
		target.context = context;
	}

	/**
	 * This is the meat of the Directive 
	 * 
	 * @param context The context to execute within
	 * @throws MergeException on processing errors
	 */
	public abstract void execute(Merger context) throws MergeException;

	/**
	 * @return Directive Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Merge Context
	 */
	public Merger getContext() {
		return context;
	}

	/**
	 * @return member of DIRECTIVE_TYPES
	 */
	public int getState() {
		return state;
	}

	/**
	 * @return member of DIRECTIVE_TYPES
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the Template (Owner)
	 */
	public Template getTemplate() {
		return template;
	}
	
	/**
	 * @param name The directive name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param type The directive type
	 */
	public void setType(int type) {
		if (DIRECTIVE_TYPES().containsKey(type)) {
			this.type = type;
		}
	}

	/**
	 * @param template The template to bind to
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	public static final int TYPE_ENRICH 			= 1;
	public static final int TYPE_INSERT				= 2;
	public static final int TYPE_PARSE	 			= 3;
	public static final int TYPE_REPLACE		 	= 4;
	public static final int TYPE_SAVE_FILE			= 5;
	public static final HashMap<Integer, String> DIRECTIVE_TYPES() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(TYPE_ENRICH, 			"enrich");
		values.put(TYPE_INSERT, 			"insert");
		values.put(TYPE_REPLACE, 			"replace");
		values.put(TYPE_PARSE,				"parse");
		values.put(TYPE_SAVE_FILE,			"save");
		return values;
	}

}
