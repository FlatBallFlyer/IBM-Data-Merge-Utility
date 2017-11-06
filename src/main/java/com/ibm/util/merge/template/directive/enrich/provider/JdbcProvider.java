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
import com.ibm.util.merge.data.parser.Parser;
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
 * A simple JDBC Provider (Not JNDI based)
 * Environment Configuration 
 *   "DATA_SOURCE": [
 *         {
 *             "credentials": {
 *                 "db_type": "",
 *                 "name": "",
 *                 "uri_cli": "",
 *                 "ca_certificate_base64": "",
 *                 "deployment_id": "",
 *                 "uri": ""
 *             },
 * @author Mike Storey
 *
 */
public class JdbcProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private final transient Merger context;
	private final transient DataSource jdbcSource;
	private final transient Connection connection;
	private final transient Parser parser;
	
	public JdbcProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
		this.parser = new Parser();
		
		String db_type;
		String name;
		String uri_cli;
		String ca_cert;
		String deployment_id;
		String uri;

		// Get Credentials
		String config = context.getConfig().getEnv(source);
		try {
			DataObject credentials = parser.parse(Parser.PARSE_JSON, config).getAsObject().get("credentials").getAsObject();
			db_type = 		credentials.get("db_type").getAsPrimitive();
			name = 			credentials.get("name").getAsPrimitive();
			uri_cli = 		credentials.get("uri_cli").getAsPrimitive();
			ca_cert = 		credentials.get("ca_certificate_base64").getAsPrimitive();
			deployment_id = 	credentials.get("deployment_id").getAsPrimitive();
			uri = 			credentials.get("uri").getAsPrimitive();
		} catch (MergeException e) {
			throw new Merge500("Invalid JDBC Provider for:" + source + "value: " + config);
		}

		// TODO - Get Connection
		try {
			jdbcSource = (DataSource) foo(db_type, name, uri_cli, ca_cert, deployment_id, uri);
			connection = jdbcSource.getConnection();
		} catch (SQLException e) {
			throw new Merge500("SQL Exception connection to data source:" + source);
		}
	}

	// TODO Remove when Get Connection comes from JDBC
	DataSource foo (String a, String b, String c, String d, String e, String f) {
		return null;
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

