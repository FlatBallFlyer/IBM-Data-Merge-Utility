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

import java.util.ArrayList;
import java.util.Iterator;

public class TemplateList implements Iterable<Template> {
	private ArrayList<Template> templates = new ArrayList<Template>();

	public void add(Template template) {
		templates.add(template);
	}
	
	public Template get(int index) {
		return templates.get(index);
	}
	
	public int size() {
		return templates.size();
	}

	public void remove(int i) {
		templates.remove(i);
	}
	
	public void addAll(TemplateList templates) {
		this.templates.addAll(templates.templates);
	}
	
	@Override
	public Iterator<Template> iterator() {
		return this.templates.iterator();
	}
}
