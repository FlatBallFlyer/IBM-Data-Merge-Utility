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
import com.ibm.util.merge.directive.provider.DataTable;

import org.apache.log4j.Logger;

/**
 * @author Mike Storey
 *
 */
public abstract class ReplaceRow extends AbstractDirective {
	private static final Logger log = Logger.getLogger( ReplaceRow.class.getName() );
	
	/**
	 * Simple constructor
	 */
	public ReplaceRow() {
		super();
	}
	
	public void copyFieldsFrom(ReplaceRow from) {
		this.copyFieldsFrom((AbstractDirective)from);
	}

	/**
	 * @throws MergeException
	 * @param tf
	 * @param rtc
	 */
	@Override
	public void executeDirective(MergeContext rtc) throws MergeException {
		AbstractProvider provider = getProvider();
		provider.getData(rtc);

		// Make sure we got some data
		if (getProvider().size() < 1 ) {
			if (!(isSoftFail() || isSoftFailTemplate())) {
				throw new MergeException(this, null, "No Data Found in " + getTemplate().getFullName(), provider.getQueryString());
			} else {
				log.warn("Softfail on Empty Resultset - " + provider.getQueryString());
				return;
			}
		}

		// Make sure we don't have a multi-table result.
		if ( provider.size() > 1 ) {
			if (!(isSoftFail() || isSoftFailTemplate())) {
				throw new MergeException(this, null, "Multi-Talbe Empty Result set returned by Directive",provider.getQueryString());
			}
			log.warn("Softfail on Multi-Table Resultset - " + provider.getQueryString());
		}
		DataTable table = provider.getTable(0);

		// Make sure we don't have an empty result set
		if ( table.size() == 0 ) {
			if (!(isSoftFail() || isSoftFailTemplate())) {
				throw new MergeException(this, null, "Empty Result set returned by Directive",provider.getQueryString());
			} else {
				log.warn("Softfail on Empty Resultset - " + provider.getQueryString());
				return;
			}
		}

		// Make sure we don't have a multi-row result set
		if ( table.size() > 1 ) {
			if (!(isSoftFail() || isSoftFailTemplate())) {
				throw new MergeException(this, null, "Multiple rows returned when single row expected", provider.getQueryString());
			}
			log.warn("Softfail on Multi-Row Resultset - " + provider.getQueryString());
		}
		
		// Add the replace values
		for (int col=0; col < table.cols(); col++) {
			getTemplate().addReplace(table.getCol(col),table.getValue(0, col));
		}
		
		log.info("Values added by Replace Row:" + String.valueOf(table.cols()));
	}

    public String getD1() {
    	return "";
    }
	public void setD1(String value) {
		return;
	}
	public String getD2() {
    	return "";
	}
	public void setD2(String value) {
		return;
	}
	public String getD3() {
		return "";
	}
	public void setD3(String value) {
		return;
	}
	public String getD4() {
		return "";
	}
	public void setD4(String value) {
		return;
	}
}
