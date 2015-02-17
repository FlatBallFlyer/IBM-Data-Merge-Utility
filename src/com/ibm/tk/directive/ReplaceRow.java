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

package com.ibm.tk.directive;
import com.ibm.tk.ConnectionFactory;
import com.ibm.tk.Template;
import com.ibm.tk.SqlQuery;
import com.ibm.tk.tkException;
import com.ibm.tk.tkSqlException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * <p>This class represents a replaceRow directive which loads the Replace hashmap
 * with the values of "{columnName}"-> column.Value in a single row sql result set.</p>
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Directive
 * @see     Merge
 */
public class ReplaceRow {
	private static final Logger log = Logger.getLogger( ReplaceRow.class.getName() );
	private SqlQuery theQuery;

	/**
	 * <p>Constructor</p>
	 *
	 * @param  sql Result Set row
	 * @throws Exception - Malformed Bookmark
	 * @return The new bookmark object
	 * @throws SQLException - Empty Row or Missing Column - Template Data Error
	 */
	public ReplaceRow(ResultSet dbRow) throws tkException  {
		try {
			this.theQuery 	= new SqlQuery( dbRow.getString("selectColumns"),
											dbRow.getString("fromTables"),
											dbRow.getString("whereCondition"));
		} catch (SQLException e) {
			throw new tkException("Replace Row Error: "+e.getMessage(), "Invalid Directive Data");
		}		
	}

	/**
	 * <p>Get Values will execute the SQL query add values to the provided HashMap</p>
	 * 
	 * @param  Reference to current Replace Values hashmap
	 * @throws SQLException on Datasource Connection and execution
	 * @throws tkException - Data Source Error if result set rows != 1 
	 * @throws tkSqlException - Database Connection Error
	 */
	public void getValues(Template target) throws tkException, tkSqlException {
		log.fine("Adding Replace Row to " + target.getFullName());
		try {
			String queryString = this.theQuery.queryString(target.getReplaceValues());
			Connection con = ConnectionFactory.getDataConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(queryString);
			rs.next();
			if ( rs.isBeforeFirst() ) {
				throw new tkException("Empty Result set returned by:" + queryString , "Data Source Error");
			}
			if ( !rs.isLast() ) {
				throw new tkException("Multiple rows returned when single row expected:" + queryString, "Data Source Error");
			}
			
			target.addRowReplace(rs);
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			throw new tkException("Replace Row Error: "+e.getMessage(), "Invalid Merge Data");
		}
	}		
}
