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
package com.ibm.util.merge.template;

public class TemplateName implements Comparable<TemplateName> {
	private final String collection;
	private final String name;
	private final String columnValue;
	public TemplateName(String collection, String name, String columnValue) {
		this.collection = collection;
		this.name = name;
		this.columnValue = columnValue;
	}
	public TemplateName(Template template) {
		this.collection = template.getCollection();
		this.name = template.getName();
		this.columnValue = template.getColumnValue();
	}
	public String getFullName() {
		return this.collection + "." + this.name + '.' + this.columnValue;
	}
	public String getCollection() {
		return collection;
	}
	public String getName() {
		return name;
	}
	public String getColumnValue() {
		return columnValue;
	}
	@Override
	public int compareTo(TemplateName that) {
		return this.getFullName().compareTo(that.getFullName());
	}
}
