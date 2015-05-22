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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.Directive;

/**
 * @author flatballflyer
 * Data provider to drive InsertSubsIf directive - Insert sub templates if a non-blank replace value exists in the hash.
 */
public class ProviderIf extends Provider implements Cloneable {
	protected String tags;
	protected Boolean matchAll;
	
	public ProviderIf(Directive newOwner, ResultSet dbRow) throws MergeException {
		super(newOwner);
		try {
			this.tag = dbRow.getString(Directive.COL_IF_TAG);
		} catch (SQLException e) {
			throw new MergeException(e, "ProviderIf Construction SQL Error", this.getQueryString());
		}
		
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderIf clone(Directive newOwner) throws CloneNotSupportedException {
		return (ProviderIf) super.clone(newOwner);
	}

	/**
	 * Reset the table, and if the Tag exists, add a row with the tag name/value
	 */
	public void getData() throws MergeException {
		DataTable table = this.reset();
		if ( this.directive.getTemplate().hasReplaceValue(this.tag) ) {
			table.addCol(this.tag);
			ArrayList<String> row = table.getNewRow();
			row.add(this.directive.getTemplate().getReplaceValue(this.tag));
		}
	}

	@Override
	public String getQueryString() {
		return this.tag;
	}

}
