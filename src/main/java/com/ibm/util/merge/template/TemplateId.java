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
package com.ibm.util.merge.template;

/**
 * The Class TemplateId - a simple object key 
 * consisting of Group, Name and Variant
 * 
 * @author Mike Storey
 * @since: v4.0
 */
public class TemplateId {
	/**
	 * A Template Group - used by the get/put/post cache operations
	 */
	public String group = "";
	/**
	 * The Template Name
	 */
	public String name = "";
	/**
	 * The Template Variant - used with Insert VaryBy operations
	 */
	public String variant = "";
	
	/**
	 * Instantiates a new template id.
	 *
	 * @param group the group
	 * @param name the name
	 * @param variant the variant
	 */
	public TemplateId(String group, String name, String variant) {
		this.group = group;
		this.name = name;
		this.variant = variant;
	}

	/**
	 * Instantiates a new template id.
	 *
	 * @param shorthand The temlpate ID Json Value
	 */
	public TemplateId(String shorthand) {
		String[] list = shorthand.split("\\.");
		if (list.length > 0) this.group = list[0];
		if (list.length > 1) this.name = list[1];
		if (list.length > 2) {
			this.variant = list[2];
			for (int i=3; i < list.length; i++) {
				this.variant = this.variant.concat(".").concat(list[i]);
			}
		}
	}

	/**
	 * Get the template shorthand name of group.name.variant
	 * @return Template Short name
	 */
	public String shorthand() {
		return group.concat(".").concat(name).concat(".").concat(variant);
	}

}
