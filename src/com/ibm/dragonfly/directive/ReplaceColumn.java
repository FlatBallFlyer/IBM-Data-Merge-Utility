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
package com.ibm.dragonfly.directive;
import com.ibm.dragonfly.Merge;
import com.ibm.dragonfly.Template;
import com.ibm.dragonfly.tkException;
import com.ibm.dragonfly.tkSqlException;

import java.util.Map;
import java.util.logging.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	private String description;
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
			this.description	= dbRow.getString("description");
			this.theQuery = new SqlQuery( 	dbRow.getString("jndiSource"),
											dbRow.getString("selectColumns"),
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
			ResultSet rs = this.theQuery.getResultSet(replaceValues);
 			int count = 0;
			while (rs.next()) {
				count++;
				replaceValues.put("{" + rs.getString("FromValue") + "}", rs.getString("ToValue"));
			}
			log.fine("ReplaceCol added " + count + " replace values");
			this.theQuery.close();
		} catch (SQLException e) {
			throw new tkException("Replace Column Error - did you select columns fromValue or toValue? "+e.getMessage(), "Invalid Merge Data");
		}		
	}

	/**
	 * <p>Get Description</p>
	 *
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}
}
