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
import com.ibm.tk.SqlQuery;
import com.ibm.tk.Merge;
import com.ibm.tk.Template;
import com.ibm.tk.tkException;
import com.ibm.tk.tkSqlException;

import java.util.Map;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p>This class represents a replaceColumn directive which loads the Replace hashmap
 * with the values of a "from" and "to" column in a sql result set.</p>
 *
 * @author  Mike Storey
 * @version 3.0
 * @since   1.0
 * @see     Template
 * @see     Merge
 */
public class ReplaceColumn {
	private static final Logger log = Logger.getLogger( ReplaceColumn.class.getName() );
	private SqlQuery theQuery;
	
	/**
	 * <p>Constructor</p>
	 *
	 * @param  Database Result Set Row Hash 
	 * @throws Exception - Malformed Bookmark
	 * @return The new replaceColumn object
	 * @throws tkException - Invalid Row or Missing Column in Directive SQL Construction
	 */
	public ReplaceColumn(ResultSet dbRow) throws tkException  {
		try {
			this.theQuery = new SqlQuery( 	dbRow.getString("selectColumns"),
											dbRow.getString("fromTables"),
											dbRow.getString("whereCondition"));
		} catch (SQLException e) {
			throw new tkException("Replace Column Error: "+e.getMessage(), "Invalid Directive Data");
		}
	}

	/**
	 * <p>Get Values will execute the SQL query add values to the provided HashMap</p>
	 * 
	 * @param  Reference to current Replace Values hashmap
	 * @throws SQLException - Data Source connection execution error
	 * @throws tkException - Empty Result Set - Data Source Error
	 * @throws tkSqlException - Database Connection Error
	 */
	public void getValues(Map<String,String> replaceValues) throws tkException, tkSqlException {
		log.fine("Adding Replace Column values");
		try {
			String queryString = this.theQuery.queryString(replaceValues);
			Connection con = ConnectionFactory.getDataConnection();
			Statement st = con.createStatement();
 			ResultSet rs = st.executeQuery(queryString );
 			int count = 0;
			while (rs.next()) {
				count++;
				replaceValues.put(rs.getString("FromValue"), rs.getString("ToValue"));
			}
			log.fine("ReplaceCol added " + count + " replace values");
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			throw new tkException("Replace Column Error - did you select columns fromValue or toValue? "+e.getMessage(), "Invalid Merge Data");
		}		
	}
}
