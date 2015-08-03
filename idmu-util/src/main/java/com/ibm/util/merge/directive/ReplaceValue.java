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

import org.apache.log4j.Logger;

/**
 * A simple replace From with To directive 
 *
 * @author  Mike Storey
 */
public class ReplaceValue extends AbstractDirective {
	private static final Logger log = Logger.getLogger( ReplaceValue.class.getName() );
	private String from = "";
	private String to = "";

	/**
	 * Simple Constructor
	 */
	public ReplaceValue() {
		super();
		this.setDescription("Add a Repalce Value");
		setType(Directives.TYPE_REPLACE_VALUE);
		setProvider(null);
	}
	
	public ReplaceValue asNew() {
		ReplaceValue to = new ReplaceValue();
		to.copyFieldsFrom(this);
		to.setFrom(	this.getFrom());
		to.setTo(	this.getTo());
		return to;
	}

	/**
	 * Add the replace value
	 * @param tf
	 * @param rtc
	 */
	@Override
	public void executeDirective(MergeContext rtc) {
		getTemplate().addReplace(from, to);
		log.info("Replaced " + from + " with " + to);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}


}
