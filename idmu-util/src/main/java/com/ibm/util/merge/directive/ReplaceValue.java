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
import com.ibm.util.merge.ConnectionFactory;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.ZipFactory;
import org.apache.log4j.Logger;

import com.ibm.util.merge.Template;

/**
 * A simple replace From with To directive 
 *
 * @author  Mike Storey
 */
public class ReplaceValue extends Directive implements Cloneable {
	private static final Logger log = Logger.getLogger( ReplaceValue.class.getName() );
	private String from = "";
	private String to = "";

	/**
	 * Simple Constructor
	 */
	public ReplaceValue() {
		super();
		this.setType(TYPE_REPLACE_VALUE);
		this.setProvider(null);
	}

	/**
	 * Simple clone
	 */
	public ReplaceValue clone(Template owner) throws CloneNotSupportedException {
		return (ReplaceValue) super.clone();
	}

	/**
	 * Add the replace value
	 * @param tf
	 * @param cf
	 * @param zf
	 */
	public void executeDirective(TemplateFactory tf, ConnectionFactory cf, ZipFactory zf) {
		this.getTemplate().addReplace(this.from, this.to);
		log.info("Replaced " + this.from + " with " + this.to);
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
