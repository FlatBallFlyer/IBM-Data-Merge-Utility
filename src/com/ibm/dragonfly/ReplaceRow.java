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
package com.ibm.dragonfly;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * <p>This class represents a replaceRow directive which loads the Replace hashmap
 * with the values of "{columnName}" to column.Value in a single row sql result set.</p>
 *
 * @author  Mike Storey
 */
class ReplaceRow extends SqlDirective {
	private static final Logger log = Logger.getLogger( ReplaceRow.class.getName() );

	/**
	 * <p>Constructor</p>
	 *
	 * @param  dbRow sql Result Set row
	 * @throws DragonFlyException Template Data Error
	 */
	public ReplaceRow(ResultSet dbRow) throws DragonFlyException  {
		super(dbRow);
	}

	/**
	 * <p>Clone Constructor</p>
	 *
	 * @param  from object to clone 
	 */
	public ReplaceRow(ReplaceRow from) {
		super(from);
	}

	/**
	 * <p>Get Values will execute the SQL query add values to the provided HashMap</p>
	 * 
	 * @param  target The Template to place values in
	 * @throws DragonFlyException Data Source Error if result set rows != 1 
	 * @throws DragonFlySqlException Database Connection Error
	 */
	public void getValues(Template target) throws DragonFlyException, DragonFlySqlException {
		Connection con = null;
		
		try {
			String queryString = this.getQueryString(target.getReplaceValues());
			con = ConnectionFactory.getDataConnection(this.jndiSource, target.getOutputFile());
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(queryString);
			rs.next();
			
			// Make sure we don't have an empty result set
			if ( rs.isBeforeFirst() ) {
				String message = "Empty Result set returned by:" + this.getQueryString(target.getReplaceValues()); 
				log.fatal(message);
				throw new DragonFlyException(message,"Data Source Error"); 
			}
			
			// Make sure we don't have a multi-row result ste
			if ( !rs.isLast() ) {
				String message = "Multiple rows returned when single row expected:" + this.getQueryString(target.getReplaceValues());
				log.fatal(message);
				throw new DragonFlyException(message, "Data Source Error");
			}
			
			// Add the row replace
			target.addRowReplace(rs);
		} catch (SQLException e) {
			throw new DragonFlyException("Replace Row Error: "+e.getMessage(), "Invalid Merge Data");
		} finally {
			log.info("Replace Row Query " + this.getQueryString(target.getReplaceValues()) + " found and used 1 row");			
		}
	}		
}
