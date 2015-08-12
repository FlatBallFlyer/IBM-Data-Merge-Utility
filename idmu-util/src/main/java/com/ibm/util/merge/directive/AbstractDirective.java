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
import com.ibm.util.merge.directive.provider.AbstractProvider;
import com.ibm.util.merge.template.Template;

/**
 * A merge directive which drive the merge process for a template 
 *
 * @author  Mike Storey
 */
public abstract class AbstractDirective {
	// Attributes
	private transient Template 	template;
	private transient long 		idTemplate 	= 0;
	private transient int		sequence	= 0;
	private int 				type		= 0;
	private boolean 			softFail	= false;
	private String 				description	= getClass().getName();
	private AbstractProvider 	provider;
	
	/********************************************************************************
	 * Simple Constructor
	 */
	public AbstractDirective() {
	}
	
	public abstract AbstractDirective asNew();
	public abstract String getD1();
	public abstract void setD1(String value);
	public abstract String getD2();
	public abstract void setD2(String value);
	public abstract String getD3();
	public abstract void setD3(String value);
	public abstract String getD4();
	public abstract void setD4(String value);
	
	/********************************************************************************
	 * Copy from
	 */
	public void copyFieldsFrom(AbstractDirective from) {
		this.setTemplate(		null);
		this.setIdTemplate(		from.getIdTemplate());
		this.setSequence(		from.getSequence());
		this.setType(			from.getType());
		this.setSoftFail(		from.isSoftFail());
		this.setDescription(	from.getDescription());
		if (from.getProvider() == null) {
			this.setProvider(null);
		} else {
			this.setProvider(	from.getProvider().asNew());
		}
	}
	
	/********************************************************************************
	 * Abstract method to "Execute" the directive in the context of a template.
	 *
	 * @throws MergeException execution errors
	 * @param tf
	 * @param rtc
	 */
	public abstract void executeDirective(MergeContext rtc) throws MergeException;

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

	public String getSoftFail() {
		return (this.softFail ? "Y" : "N");
	}

	public boolean isSoftFail() {
		return softFail;
	}

	public int getType() {
		return type;
	}

	public String getDescription() {
    	if (description.length() > 45) {
    		return description.substring(0, 45);
    	} else {
            return description;
    	}
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

	public void setSoftFail(String softFail) {
		this.softFail = softFail.equals("Y");
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
