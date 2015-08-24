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

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.template.Template;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * @author flatballflyer
 * Data provider to drive InsertSubsIf directive - Insert sub templates if a non-blank replace value exists in the hash.
 */
public class ProviderTag extends AbstractProvider {
    private static final Logger log = Logger.getLogger(ProviderTag.class);
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
		setType(Providers.TYPE_TAG);
	}
	
	public ProviderTag asNew() {
		ProviderTag to = new ProviderTag();
		to.copyFieldsFrom(this);
		to.setTag(		this.getTag());
		to.setCondition(this.getCondition());
		to.setList(		this.isList());
		to.setValue(	this.getValue());
		return to;
	}

	/**
	 * Reset the table, and if the Tag exists, add a row with the tag name/value
	 * @param cf
	 */
	@Override
	public void getData(MergeContext rtc) throws MergeException {
		reset();
		DataTable table = addNewTable();
		Template template = getDirective().getTemplate();
		String theTag = Template.wrap(tag);
		log.info("Getting Tag Data for " + tag);
		
		switch (condition) {
		case ProviderTag.CONDITION_EXISTS:
			if (!template.hasReplaceKey(theTag)) {
				log.info("Tag not found for Exists Condition");
				return;
			}
			break;
		case ProviderTag.CONDITION_BLANK: 
			if (!template.hasReplaceKey(theTag) 
				|| template.hasReplaceValue(theTag)) {
				log.info("Tag not found or Data found for Blank Condition");
				return;
			}   
			break;
		case ProviderTag.CONDITION_NONBLANK: 
			if (!template.hasReplaceKey(theTag) 
				|| !template.hasReplaceValue(theTag)) {
				log.info("Tag or Empty Data found for Non-Blank Condition");
				return;
			}   
			break;
		case ProviderTag.CONDITION_EQUALS: 
			if (!template.hasReplaceKey(theTag) 
				|| !template.hasReplaceValue(theTag)
				|| !template.getReplaceValue(theTag).equals(value)) {
				log.info("Tag not Equals or not found");
				return;
			}   
			break;
		}
		
		// We have a match, so add data
		String data = template.getReplaceValue(Template.wrap(tag));
		log.info("Data Found: " + data);
		table.addCol(tag);
		if (isList()) {
			for (String datum : new ArrayList<>(Arrays.asList(data.split(",")))) {
				if (!datum.isEmpty()) {
					ArrayList<String> row = table.addNewRow();
					row.add(datum);
				}
			}			
		} else {
			ArrayList<String> row = table.addNewRow();
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

	public String getList() {
		return (list ? "Y" : "N");
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

	public void setCondition(String condition) {
		this.condition = Integer.parseInt(condition);
	}

	public void setList(boolean list) {
		this.list = list;
	}

	public void setList(String list) {
		this.list = list.equals("Y");
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getP1() {
		return this.getTag();
	}

	@Override
	public void setP1(String value) {
		this.setTag(value);
	}

	@Override
	public String getP2() {
		return Integer.toString(this.getCondition());
	}

	@Override
	public void setP2(String value) {
		this.setCondition(Integer.parseInt(value));
	}

	@Override
	public String getP3() {
		return this.getList();
	}

	@Override
	public void setP3(String value) {
		this.setList(value);
	}

	@Override
	public String getP4() {
		return this.getValue();
	}

	@Override
	public void setP4(String value) {
		this.setValue(value);
	}
}
