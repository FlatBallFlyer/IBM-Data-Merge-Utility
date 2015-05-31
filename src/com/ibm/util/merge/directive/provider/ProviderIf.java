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
import java.util.List;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.Directive;

/**
 * @author flatballflyer
 * Data provider to drive InsertSubsIf directive - Insert sub templates if a non-blank replace value exists in the hash.
 */
public class ProviderIf extends Provider implements Cloneable {
	private ArrayList<String> tags = new ArrayList<String>();
	private Boolean matchAll	= false;
	
	public ProviderIf() {
		super();
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderIf clone() throws CloneNotSupportedException {
		return (ProviderIf) super.clone();
	}

	/**
	 * Reset the table, and if the Tag exists, add a row with the tag name/value
	 */
	public void getData() throws MergeException {
		DataTable table = this.reset();
		// TODO - This is "Match Any" - need to add "MatchAll"
		for (String tag : this.tags) {
			if ( this.getDirective().getTemplate().hasReplaceValue(tag) ) {
				table.addCol(tag);
				ArrayList<String> row = table.getNewRow();
				row.add(this.getDirective().getTemplate().getReplaceValue(tag));
			}
		}
	}

	@Override
	public String getQueryString() {
		return this.getTags();
	}
	
	public String getTags() {
		return String.join(",", this.tags);
	}

	public void setTags(String tags) {
		this.tags = new ArrayList<String>(Arrays.asList(tags.split(",")));
	}

	public String getMatchAll() {
		return (matchAll ? "Y" : "N");
	}

	public void setMatchAll(String matchAll) {
		this.matchAll = matchAll.equals("Y");
	}


}
