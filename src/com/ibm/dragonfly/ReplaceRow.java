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
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>This class represents a replaceRow directive which loads the Replace hashmap
 * with the values of "{columnName}" to column.Value in a single row sql result set.</p>
 *
 * @author  Mike Storey
 */
class ReplaceRow extends SqlDirective {

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
	 * <p>Get Values will execute the SQL query add values to the provided HashMap</p>
	 * 
	 * @param  target The Template to place values in
	 * @throws DragonFlyException Data Source Error if result set rows != 1 
	 * @throws DragonFlySqlException Database Connection Error
	 */
	public void getValues(Template target) throws DragonFlyException, DragonFlySqlException {
		try {
			ResultSet rs = this.getResultSet(target.getReplaceValues());
			rs.next();
			if ( rs.isBeforeFirst() ) {
				throw new DragonFlyException(
						"Empty Result set returned by:" + 
						this.getQueryString(target.getReplaceValues()), 
						"Data Source Error");
			}
			if ( !rs.isLast() ) {
				throw new DragonFlyException(
						"Multiple rows returned when single row expected:" + 
						this.getQueryString(target.getReplaceValues()), 
						"Data Source Error");
			}
			
			target.addRowReplace(rs);
			this.close();
		} catch (SQLException e) {
			throw new DragonFlyException("Replace Row Error: "+e.getMessage(), "Invalid Merge Data");
		}
	}		
}
