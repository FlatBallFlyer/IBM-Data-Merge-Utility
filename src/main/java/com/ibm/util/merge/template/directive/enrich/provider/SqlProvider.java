/*
 * 
 * Copyright 2015-2017 IBM
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
package com.ibm.util.merge.template.directive.enrich.provider;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

/**
 * A common base clase for JNDI and JDBC Providers
 * 
 * @author Mike Storey
 *
 */
public abstract class SqlProvider implements ProviderInterface {
	protected final String source;
	protected final String dbName;
	protected final transient Merger context;
	protected transient DataSource jdbcSource = null;
	protected transient Connection connection = null;
	
	public SqlProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}
	
	protected abstract void connect() throws MergeException;
	
	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		if (this.connection == null) {
			connect();
		}
		
		Content query = new Content(wrapper, command, TagSegment.ENCODE_SQL);
		query.replace(replace, false, Config.nestLimit());
		DataList table = new DataList();
		ResultSet results;

		// Execute Command
		try {
			results = this.connection.
					prepareStatement(query.getValue(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).
					executeQuery();
		} catch (SQLException e) {
			throw new Merge500("Invalid SQL Query " + query.getValue() + " Message:" + e.getMessage());
		}

		// Build result table.
		try {
			ResultSetMetaData meta = results.getMetaData();
			while (results.next()) {
				DataObject row = new DataObject();
				for (int column = 1; column <= meta.getColumnCount(); column++) {
					row.put(meta.getColumnName(column), new DataPrimitive(results.getString(column)));
				}
				table.add(row);
			}
			results.close();
		} catch (SQLException e) {
			throw new Merge500("SQL Exception processing results" + query );
		}
		return table;
	}

	@Override
	public void close() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				// TODO Log Exception?
			}
		}
		return;
	}
	
	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public String getDbName() {
		return this.dbName;
	}

	@Override
	public Merger getContext() {
		return this.context;
	}
}

