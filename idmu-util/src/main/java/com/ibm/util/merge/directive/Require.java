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

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.template.Template;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A validation directive 
 * Allows a template to specify a set of replace values that must be on the 
 * replace stack. Supports more meaningful error messages on merge failures.
 * 
 * DEFERRED IMPLEMENTATION
 *
 * @author  Mike Storey
 */
public class Require extends AbstractDirective {
	private ArrayList<String> tags = new ArrayList<>();
	
	/**
	 * Simple Constructor
	 */
	public Require() {
		super();
		setType(Directives.TYPE_REQUIRE);
		setProvider(null);
	}
	
	public Require asNew() {
		Require to = new Require();
		to.copyFieldsFrom((AbstractDirective)this);
		to.setTags(	this.getTags());
		return to;
	}
	
	/**
	 * Check to see if the tags are in the replace stack, throw an exception if not found
	 * @see AbstractDirective#executeDirective(MergeContext)
	 * @param tf
	 * @param rtc
	 */
	@Override
	public void executeDirective(MergeContext rtc) throws MergeException {
		for (String tag : tags) {
			if (!getTemplate().hasReplaceValue(Template.wrap(tag)) ) {
				throw new MergeException("Required Tag Not Found in " + getTemplate().getFullName(), tag);
			}
		}
	}

	public String getTags() {
		return String.join(",", tags);
	}

	public void setTags(String tags) {
		this.tags = new ArrayList<>(Arrays.asList(tags.split(",")));
	}


}
