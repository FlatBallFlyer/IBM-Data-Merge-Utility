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

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.DataTable;

/**
 * The ReplaceCol directive adds values to the Template Replace hash based on From and To 
 * column names. For multi-table data sets, each table that contains the columns is processed.
 * 
 * @author Mike Storey
 */
public abstract class ReplaceCol extends Directive implements Cloneable {
	private String fromColumn;
	private String toColumn;
	
	/**
	 * Database Constructor
	 * 
	 * @param dbRow the Sql ResultSet row with Directive values
	 * @param owner the Template that owns this Directive
	 * @throws MergeException on Database access errors
	 */
	public ReplaceCol(ResultSet dbRow, Template owner) throws MergeException {
		super(dbRow, owner);
		try {
			this.fromColumn = dbRow.getString(Directive.COL_REPLACE_COLUMN_FROM);
			this.toColumn = dbRow.getString(Directive.COL_REPLACE_COLUMN_TO);
		} catch (SQLException e) {
			throw new MergeException(e, "Replace Col Constructor Error", this.getFullName());
		}
	}
	
	/**
	 * Clone constructor
	 */
	public ReplaceCol clone() throws CloneNotSupportedException {
		return (ReplaceCol) super.clone();
	}
	
	/**
	 * Add replace values to the Template replace stack
	 * @throws MergeException on getData errors.
	 */
	public void executeDirective() throws MergeException {
		this.provider.getData();
		for (DataTable table : this.provider.getTables() ) {
	 		int from = table.getCol(this.fromColumn);
			int to = table.getCol(this.toColumn);
			if (from > 0 && to > 0) {
				for (int row=1; row < table.size(); row++) {
					this.template.addReplace(table.getValue(row, from),table.getValue(row, to));
				}
			}
		}
	}
}
