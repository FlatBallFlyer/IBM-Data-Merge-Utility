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
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <p>A simple JDBC Provider. 
 * This provider does not utilize a connection pool and as such is not well suited to high performance requirements.
 * If you have high performance requirements, see the {@link com.ibm.util.merge.template.directive.enrich.provider.JndiProvider}</p> 
 * 
 * <p>Provide Parameters usage</p>
 * <ul>
 * 		<li>String command - A SQL Select Statement. Can contain Tags, all Tags are processed with SQL Encoding</li>
 * 		<li>Wrapper wrapper - Wrapper for tags </li>
 * 		<li>Merger context - Merger managing the merge</li>
 * 		<li>HashMap&lt;String,String&gt; replace - The Replace HashMap used to process tags in command</li>
 * 		<li>int parseAs - Not Applicable / Used</li>
 * </ul>
 * <p>Configuration Environment Variables</p>
 * <ul>
 *		<li>{source}.URI - Database Connection URI, without UserName/PW components</li>
 *		<li>{source}.USER - The Database User ID to use</li>
 *		<li>{source}.PW - The Password for the User ID</li>
 * </ul>
 * @author Mike Storey
 *
 */
public class JdbcProvider extends SqlProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Database",
			"The following environment variables are expected\n"
			+ "{source}.URI - Database Connection URI, without UserName/PW components\n"
			+ "{source}.USER - The Database User ID to use\n"
			+ "{source}.PW - The Password for the User ID", 
			"A SQL Select Statement - Replace Tags are supported and SQL encoded",
			"N/A",
			"Always returns a List of Object");
	
	/**
	 * JDBC Provider Constructor
	 * @param source The jdbc environment variable name
	 * @param parameter The database - ignored if blank
	 */
	public JdbcProvider(String source, String parameter) {
		super(source, parameter);
	}
	
	@Override
	protected void connect(Config config) throws MergeException {
		// Implements lazy connetor
		if (this.connection != null) return;
		
		// Get Credentials
		String uri = "";
		String user = "";
		String pw = "";
		try {
			uri = config.getEnv(source + ".URI");
			user = config.getEnv(source + ".USER");
			pw = config.getEnv(source + ".PW");
		} catch (MergeException e) {
			throw new Merge500("JDBC Provider did not find environment variables:" + source + ":" + uri + ":" + user + ":" + pw);
		}

		// Get Connection
		try {
		    connection = DriverManager.getConnection(uri, user, pw);
		    if (!parameter.isEmpty()) {
		    		this.connection.prepareStatement("USE " + parameter).execute();
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

