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
package com.ibm.util.merge.template.directive;

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

/**
 * The Class AbstractDirective is the base class of the merge directives
 * which are merge instructions used during the merge process.
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public abstract class AbstractDirective {

	public static final int TYPE_ENRICH 			= 1;
	public static final int TYPE_INSERT				= 2;
	public static final int TYPE_REMOVE_BOOKMARKS	= 3;
	public static final int TYPE_REPLACE		 	= 4;
	public static final int TYPE_REPLACE_PROCESS	= 5;
	public static final int TYPE_REQUIRE 			= 6;
	public static final int TYPE_SAVE_FILE 			= 7;
	public static final int TYPE_PARSE	 			= 8;
	public static final HashMap<Integer, String> DIRECTIVE_TYPES() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(TYPE_ENRICH, 			"enrich");
		values.put(TYPE_INSERT, 			"insert");
		values.put(TYPE_REMOVE_BOOKMARKS, 	"remove-bookmarks");
		values.put(TYPE_REPLACE, 			"replace");
		values.put(TYPE_REPLACE_PROCESS,	"replace-process");
		values.put(TYPE_REQUIRE,			"require");
		values.put(TYPE_SAVE_FILE,			"save-file");
		values.put(TYPE_PARSE,				"parse");
		return values;
	}

	protected transient DataProxyJson gson;
	protected transient Template template;
	protected int type;
	protected String name = "";

	public abstract void execute(Merger context) throws MergeException;
	public abstract AbstractDirective getMergable() throws MergeException;

	public AbstractDirective() {
		this.gson = new DataProxyJson();
		this.template = null;
	}
	
	public void makeMergable(AbstractDirective target) {
		target.setType(this.getType());
	}
	
	public Template getTemplate() {
		return template;
	}
	
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		if (DIRECTIVE_TYPES().containsKey(type)) {
			this.type = type;
		}
	}
	
}
