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
package com.ibm.util.merge.directive.provider;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.directive.AbstractDirective;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author flatballflyer
 */
public class ProviderSql extends AbstractProvider implements Cloneable {
    private static final Logger log = Logger.getLogger(ProviderSql.class.getName());
    private String source = "";
    private String columns = "";
    private String from = "";
    private String where = "";

    /**
     * Simple constructor
     */
    public ProviderSql() {
        super();
        setType(Providers.TYPE_SQL);
    }

    /**
     * Simple clone method
     *
     * @see AbstractProvider#clone(AbstractDirective)
     */
    @Override
    public ProviderSql clone() throws CloneNotSupportedException {
        ProviderSql provider = (ProviderSql) super.clone();
        return provider;
    }

    /**
     * Prepare and execute a SQL statement, then load the result set into a single DataTable
     *
     * @param cf
     * @throws MergeException Wrapped SQL and Process execptions
     */
    @Override
    public void getData(RuntimeContext rtc) throws MergeException {
		this.reset();
		DataTable table = this.addNewTable();
		Connection con = null;
		
		try {
			// Prepare and Execute the SQL Statement
			String queryString = this.getQueryString();
			log.info(this.getDirective().getTemplate().getFullName() + " Selecting SQL Data " + queryString );
			
			con = rtc.getConnection(this.source);
			PreparedStatement st = con.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery();
			
			// Populate the Table Column names
			ResultSetMetaData meta = rs.getMetaData();
			final int columnCount = meta.getColumnCount();
		    for (int column = 1; column <= columnCount; column++) 
		    {
		    	table.addCol(meta.getColumnName(column));
		    }
			
			// Populate the Table Data
			while (rs.next() ) {
				ArrayList<String> row = table.addNewRow();
			    for (int column = 1; column <= columnCount; column++) 
			    {
			    	String value = rs.getString(column);
			    	row.add( value != null ? value : "");
			    }
			}
		} catch (SQLException e) {
			throw new MergeException(e, "Invalid Merge Data", this.getQueryString() );
		} finally {
			log.info("Sql Dataprovider read " + Integer.toString(table.size()) + " rows");			
		}
    }

    /**
     * Get the select statement
     *
     * @param replaceValues Replace Hash to use
     * @return the Select Statement
     */
    @Override
    public String getQueryString() {
        String query = "SELECT " + getDirective().getTemplate().replaceProcess(columns);
        if (!from.isEmpty()) {
            query += " FROM " + getDirective().getTemplate().replaceProcess(from);
        }
        if (!where.isEmpty()) {
            query += " WHERE " + getDirective().getTemplate().replaceProcess(where);
        }
        return query;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
