/*
/*
 * Copyright (c) 2015 IBM 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package com.ibm.tk;

import java.util.Map;

/**
 * A simple wrapper for the Template Select Query builder 
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     InsertRows
 * @see 	ReplaceRow
 * @see 	ReplaceCol
 */
public class SqlQuery {
	private String selectColumns;
	private String fromTables;
	private String whereCondition;

	/**
	 * Simple constructor 
	 *
	 */
	public SqlQuery(String columns, String table, String where ) {
		this.selectColumns = columns;
		this.fromTables = table;
		this.whereCondition = where;
	}
	
	/**
	 * Get the select query string. Note the WHERE clause will
	 * process the replace values Hash before returning. 
	 *
	 */
	public String queryString(Map<String,String> replaceValues) {
		String queryString = "SELECT " + this.selectColumns + " FROM " + this.fromTables;
		if ( !this.whereCondition.isEmpty() ) {

			// run replace stack over where condition
			String where = this.whereCondition;
			for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
				  where = where.replace(entry.getKey(), entry.getValue());
				}
			queryString += " WHERE " + where;
		}
		return queryString;
	}
}
