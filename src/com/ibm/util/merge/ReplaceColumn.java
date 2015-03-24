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
package com.ibm.util.merge;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

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
class ReplaceColumn extends SqlDirective {
	private static final Logger log = Logger.getLogger( ReplaceColumn.class.getName() );
	
	/**
	 * <p>Database Constructor</p>
	 *
	 * @param  dbRow Database Result Set Row Hash 
	 * @throws DragonFlyException Invalid Row or Missing Column in Directive SQL Construction
	 */
	public ReplaceColumn(ResultSet dbRow) throws DragonFlyException  {
		super(dbRow);
	}

	/**
	 * <p>Clone Constructor</p>
	 *
	 * @param  from object to clone 
	 */
	public ReplaceColumn(ReplaceColumn from) {
		super(from);
	}

	/**
	 * <p>Get Values will execute the SQL query add values to the provided HashMap</p>
	 * 
	 * @param  target The template to place values in
	 * @throws DragonFlyException Empty Result Set Data Source Error
	 * @throws DragonFlySqlException Database Connection Error
	 */
	public void getValues(Template target) throws DragonFlyException, DragonFlySqlException {
		int count = 0;
		Connection con = null;
		
		try {
			String queryString = this.getQueryString(target.getReplaceValues());
			con = ConnectionFactory.getDataConnection(this.jndiSource, target.getOutputFile());
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(queryString);
			while (rs.next()) {
				target.addColReplace(rs.getString("FromValue"), rs.getString("ToValue"));
				count++;
			}
		} catch (SQLException e) {
			throw new DragonFlyException("Replace Column Error - did you select columns fromValue or toValue? "+e.getMessage(), "Invalid Merge Data");
		} finally {
			log.info("Added " + String.valueOf(count) + " Replace Column values");
		}
	}
}
