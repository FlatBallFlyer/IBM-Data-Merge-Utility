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
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A simple JDBC Provider (Not JNDI based)
 * <p>Environment Variable Format<blockquote><pre>
 *   "DATA_SOURCE": [
 *		{
 *      	"credentials": {
 *          	"uri_cli": "",
 * 		}
 * ]
 * </pre></blockquote>
 * 
 * @author Mike Storey
 *
 */
public class JdbcProvider extends SqlProvider implements ProviderInterface {
	private final DataProxyJson proxy = new DataProxyJson();
	private static final ProviderMeta meta = new ProviderMeta(
			"Database",
			"Credentials", 
			"Command Help",
			"Parse Help",
			"Return Help");
	
	class Credentials {
		public String uri_cli;
	}

	public JdbcProvider(String source, String dbName, Merger context) throws MergeException {
		super(source, dbName, context);
	}
	
	@Override
	protected void connect() throws MergeException {
		// Get Credentials
		Credentials creds;
		try {
			creds = proxy.fromString(Config.env(source), Credentials.class);
		} catch (Throwable e) {
			throw new Merge500("Invalid JDBC Provider for:" + this.source + " Message:" + e.getMessage());
		}

		// Get Connection
		try {
		    connection = DriverManager.getConnection( creds.uri_cli);
		} catch (SQLException e) {
			throw new Merge500("SQL Exception connection to data source:" + source + " Message:" + e.getMessage());
		}
	}
	
	@Override
	public ProviderMeta getMetaInfo() {
		return JdbcProvider.meta;
	}
}

