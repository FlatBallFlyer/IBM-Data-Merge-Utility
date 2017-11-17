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
 * The Class AbstractDirective is the base class of the merge directives
 * which are merge instructions used during the merge process.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
/**
 * @author flatballflyer
 *
 */
public abstract class AbstractDirective {

	/**
	 * The template that possess this directive
	 */
	private transient Template template;
	/**
	 * The template type - See TYPE_* 
	 */
	private int type;
	/**
	 * The directive name - used logging and exception handling
	 */
	private String name = "";

	/**
	 * Instantiate a Directive 
	 */
	public AbstractDirective() {
		this.template = null;
	}
	
	/**
	 * Get a mergable copy of this directive
	 * 
	 * @param target The directive to make mergable
	 */
	public void makeMergable(AbstractDirective target) {
		target.setType(this.getType());
		target.setName(name);
	}
	
	/**
	 * @return the Template (Owner)
	 */
	public Template getTemplate() {
		return template;
	}
	
	/**
	 * @param template The template to bind to
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	/**
	 * @return Directive Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The directive name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return member of DIRECTIVE_TYPES
	 */
	public int getType() {
		return type;
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
	 * This is the meat of the Directive 
	 * 
	 * @param context The context to execute within
	 * @throws MergeException on processing errors
	 */
	public abstract void execute(Merger context) throws MergeException;

	/**
	 * Each directive must implement a clone-like get mergable
	 * @return the Mergable directive
	 * @throws MergeException on processing errors
	 */
	public abstract AbstractDirective getMergable() throws MergeException;

	/**
	 * Populate Transient Values and validate enumerations
	 * @param template The template to bind to
	 * @throws MergeException when enumirators fail to validate
	 */
	public void cachePrepare(Template template) throws MergeException {
		// TODO Validate Enums
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
