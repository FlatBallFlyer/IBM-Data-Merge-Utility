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
import com.ibm.util.merge.directive.provider.DataTable;

/**
 * The ReplaceCol directive adds values to the Template Replace hash based on From and To 
 * column names. For multi-table data sets, each table that contains the columns is processed.
 * 
 * @author Mike Storey
 */
public abstract class ReplaceCol extends AbstractDirective {
	private String fromColumn	= "";
	private String toColumn		= "";
	
	/**
	 * Simple Constructor
	 */
	public ReplaceCol() {
		super();
	}
	
	public void copyFieldsFrom(ReplaceCol from) {
		this.copyFieldsFrom((AbstractDirective)from);
		this.setFromColumn(	from.getFromColumn());
		this.setToColumn(	from.getToColumn());
	}
	
	/**
	 * Add replace values to the Template replace stack
	 * @throws MergeException on getData errors.
	 * @param tf
	 * @param rtc
	 */
	@Override
	public void executeDirective(MergeContext rtc) throws MergeException {
		getProvider().getData(rtc);
		for (DataTable table : getProvider().getTables() ) {
	 		int from = table.getCol(fromColumn);
			int to = table.getCol(toColumn);
			if (from > -1 && to > -1) {
				for (int row=0; row < table.size(); row++) {
					getTemplate().addReplace(table.getValue(row, from),table.getValue(row, to));
				}
			}
		}
	}

	public String getFromColumn() {
		return fromColumn;
	}

	public void setFromColumn(String fromColumn) {
		this.fromColumn = fromColumn;
	}

	public String getToColumn() {
		return toColumn;
	}

	public void setToColumn(String toColumn) {
		this.toColumn = toColumn;
	}

    public String getD1() {
    	return this.getFromColumn();
    }
	public void setD1(String value) {
		this.setFromColumn(value);
	}
	public String getD2() {
		return this.getToColumn();
	}
	public void setD2(String value) {
		this.setToColumn(value);
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
