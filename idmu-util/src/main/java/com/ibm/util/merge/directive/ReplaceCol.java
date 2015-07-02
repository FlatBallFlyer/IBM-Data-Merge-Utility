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

import com.ibm.util.merge.*;
import com.ibm.util.merge.directive.provider.DataTable;

/**
 * The ReplaceCol directive adds values to the Template Replace hash based on From and To 
 * column names. For multi-table data sets, each table that contains the columns is processed.
 * 
 * @author Mike Storey
 */
public abstract class ReplaceCol extends Directive implements Cloneable {
	private String fromColumn	= "";
	private String toColumn		= "";
	
	/**
	 * Simple Constructor
	 */
	public ReplaceCol() {
		super();
	}
	
	/**
	 * Clone constructor
	 */
	public ReplaceCol clone(Template owner) throws CloneNotSupportedException {
		return (ReplaceCol) super.clone();
	}
	
	/**
	 * Add replace values to the Template replace stack
	 * @throws MergeException on getData errors.
	 * @param tf
	 * @param cf
	 * @param zf
	 */
	public void executeDirective(TemplateFactory tf, ConnectionFactory cf, ZipFactory zf) throws MergeException {
		this.getProvider().getData(cf);
		for (DataTable table : this.getProvider().getTables() ) {
	 		int from = table.getCol(this.fromColumn);
			int to = table.getCol(this.toColumn);
			if (from > -1 && to > -1) {
				for (int row=0; row < table.size(); row++) {
					this.getTemplate().addReplace(table.getValue(row, from),table.getValue(row, to));
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

}
