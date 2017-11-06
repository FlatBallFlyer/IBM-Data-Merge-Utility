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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Implements JNDI based data enrichment
 * 
 * @author flatballflyer
 *
 */
public class JndiProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Merger context;
//	private transient final DataProxyJson proxy = new DataProxyJson();
	private transient final DataSource jndiSource;
	private transient final Connection connection;
	
	public JndiProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;

		// Get Connection
    	try {
	    	Context initContext = new InitialContext();
	    	this.jndiSource = (DataSource) initContext.lookup(this.getSource());
	    	this.connection = this.jndiSource.getConnection();
	    } catch (NamingException e) {
	    	throw new Merge500("Naming Exception: " + this.getSource());
	    } catch (SQLException e) {
	    	throw new Merge500("Error acquiring connection for " + source + ":" + e.getMessage());
	    }
	}

	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws MergeException {
		Content query = new Content(wrapper, enrichCommand, TagSegment.ENCODE_SQL);
		query.replace(replace, false, context.getConfig().getNestLimit());
		DataList table = new DataList();
		ResultSet results;

		// Execute Command
		try {
			results = this.connection.
					prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).
					executeQuery();
		} catch (SQLException e) {
			throw new Merge500("Invalid SQL Query " + query );
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

