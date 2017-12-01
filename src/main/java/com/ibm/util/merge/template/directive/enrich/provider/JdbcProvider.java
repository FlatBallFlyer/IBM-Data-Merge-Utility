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
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A simple JDBC Provider (Not JNDI based)
 * <p>Environment Variable Format<blockquote><pre>
 *	{source}.URI
 *	{source}.USER
 *	{source}.PW
 * </pre></blockquote>
 * 
 * @author Mike Storey
 *
 */
public class JdbcProvider extends SqlProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Database",
			"Credentials", 
			"Command Help",
			"Parse Help",
			"Return Help");
	
	public JdbcProvider(String source, String dbName, Merger context) throws MergeException {
		super(source, dbName, context);
	}
	
	@Override
	protected void connect() throws MergeException {
		// Get Credentials
		String uri = "";
		String user = "";
		String pw = "";
		try {
			uri = Config.env(source + ".URI");
			user = Config.env(source + ".USER");
			pw = Config.env(source + ".PW");
		} catch (MergeException e) {
			throw new Merge500("JDBC Provider did not find environment variables:" + source + ":" + uri + ":" + user + ":" + pw);
		}

		// Get Connection
		try {
		    connection = DriverManager.getConnection(uri, user, pw);
		    if (!this.dbName.isEmpty()) {
		    	this.connection.prepareStatement("USE " + this.dbName).execute();
		    }
		    connection.setReadOnly(true);
		} catch (SQLException e) {
			throw new Merge500("SQL Exception connection to data source:" + source + ":" + " Message:" + e.getMessage()+ ":" + uri + ":" + user + ":" + pw);
		}
		
		
	}
	
	@Override
	public ProviderMeta getMetaInfo() {
		return JdbcProvider.meta;
	}
}

