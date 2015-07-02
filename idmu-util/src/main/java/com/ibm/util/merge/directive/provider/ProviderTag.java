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
package com.ibm.util.merge.directive.provider;

import java.util.ArrayList;
import java.util.Arrays;

import com.ibm.util.merge.ConnectionFactory;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;

/**
 * @author flatballflyer
 * Data provider to drive InsertSubsIf directive - Insert sub templates if a non-blank replace value exists in the hash.
 */
public class ProviderTag extends Provider implements Cloneable {
	public static final int CONDITION_EXISTS = 0;
	public static final int CONDITION_BLANK = 1;
	public static final int CONDITION_NONBLANK = 2;
	public static final int CONDITION_EQUALS = 3;
	private String tag = "";
	private int condition = 0;
	private boolean list = false;
	private String value = "";
	
	public ProviderTag() {
		super();
		this.setType(Provider.TYPE_TAG);
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderTag clone() throws CloneNotSupportedException {
		return (ProviderTag) super.clone();
	}

	/**
	 * Reset the table, and if the Tag exists, add a row with the tag name/value
	 * @param cf
	 */
	public void getData(ConnectionFactory cf) throws MergeException {
		this.reset();
		DataTable table = this.getNewTable();
		Template template = this.getDirective().getTemplate();
		String theTag = Template.wrap(this.tag);
		
		switch (this.condition) {
		case ProviderTag.CONDITION_EXISTS:
			if (!template.hasReplaceKey(theTag)) {
				return;
			}
			break;
		case ProviderTag.CONDITION_BLANK: 
			if (!template.hasReplaceKey(theTag) 
				|| template.hasReplaceValue(theTag)) {
				return;
			}   
			break;
		case ProviderTag.CONDITION_NONBLANK: 
			if (!template.hasReplaceKey(theTag) 
				|| !template.hasReplaceValue(theTag)) {
				return;
			}   
			break;
		case ProviderTag.CONDITION_EQUALS: 
			if (!template.hasReplaceKey(theTag) 
				|| !template.hasReplaceValue(theTag)
				|| !template.getReplaceValue(theTag).equals(this.value)) {
				return;
			}   
			break;
		}
		
		// We have a match, so add data
		String data = template.getReplaceValue(Template.wrap(tag));
		table.addCol(tag);
		if (this.isList()) {
			for (String datum : new ArrayList<String>(Arrays.asList(data.split(",")))) {
				if (!datum.isEmpty()) {
					ArrayList<String> row = table.getNewRow();
					row.add(datum);
				}
			}			
		} else {
			ArrayList<String> row = table.getNewRow();
			row.add(data);
		}
	}

	@Override
	public String getQueryString() {
		return tag;
	}
	
	public String getTag() {
		return tag;
	}

	public int getCondition() {
		return condition;
	}

	public boolean isList() {
		return list;
	}

	public String getValue() {
		return value;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public void setList(boolean list) {
		this.list = list;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
