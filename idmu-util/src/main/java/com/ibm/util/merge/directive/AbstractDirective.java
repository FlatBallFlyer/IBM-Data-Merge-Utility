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

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.directive.provider.AbstractProvider;

/**
 * A merge directive which drive the merge process for a template 
 *
 * @author  Mike Storey
 */
public abstract class AbstractDirective implements Cloneable{
	// Attributes
	private transient Template 	template;
	private transient long 		idTemplate 	= 0;
	private transient int			sequence	= 0;
	private int 		type		= 0;
	private boolean 	softFail	= false;
	private String 		description	= getClass().getName();
	private AbstractProvider provider;
	
	/********************************************************************************
	 * Simple Constructor
	 */
	public AbstractDirective() {
	}
	
	/********************************************************************************
	 * Abstract method to "Execute" the directive in the context of a template.
	 *
	 * @throws MergeException execution errors
	 * @param tf
	 * @param rtc
	 */
	public abstract void executeDirective(RuntimeContext rtc) throws MergeException;

	/********************************************************************************
	 * Cone constructor
	 * @throws CloneNotSupportedException
	 */
	@Override
	public AbstractDirective clone() throws CloneNotSupportedException {
		AbstractDirective newDirective = (AbstractDirective) super.clone();
		if (provider != null) {
			newDirective.setProvider( (AbstractProvider) provider.clone() );
		}
		newDirective.template = null;
		return newDirective;
	}

	public boolean isSoftFailTemplate() {
		return template.isSoftFail();
	}

	/**
	 * @return the Template Fullname + the Directive Description
	 */
	public String getFullName() {
		return template.getFullName() + ":Directive-" + description;
	}
	
	public Template getTemplate() {
		return template;
	}
	
	public AbstractProvider getProvider() {
		return provider;
	}

	public boolean isSoftFail() {
		return softFail;
	}

	public int getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}
	public long getIdTemplate() {
		return idTemplate;
	}

	public void setIdTemplate(long idTemplate) {
		this.idTemplate = idTemplate;
	}

	public void setProvider(AbstractProvider provider) {
		this.provider = provider;
		if (provider != null) {
			provider.setDirective(this);
		}
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setSoftFail(boolean softFail) {
		this.softFail = softFail;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}	
}
