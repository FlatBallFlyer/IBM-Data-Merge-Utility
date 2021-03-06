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
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * <p>A JDBC Provider that uses a JNDI Naming context to access a connection pool.</p>  
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
 *		<li>Not Applicable - Source provided during construction is the JNDI Data Source Name</li>
 * </ul>
 * 
 * @author Mike Storey
 *
 */
public class JndiProvider extends SqlProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Database",
			"N/A - Source specifies a JNDI Name", 
			"A SQL Select Statement",
			"N/A",
			"Always returns a List of Object");
	
	class Credentials {
		public String db_type;
		public String name;
		public String uri_cli;
		public String ca_cert;
		public String deployment_id;
		public String uri;
	}

	private transient DataSource jndiSource = null;
	
	/**
	 * Construct a Jndi Based SQL provider
	 * @param source The JNDI Data Source name
	 * @param parameter The database name, ignored if blank
	 */
	public JndiProvider(String source, String parameter) {
		super(source, parameter);
	}

	@Override
	protected void connect(Config config) throws Merge500 {
		// Implements lazy connection
		if (this.connection != null) return;
		
	    	try {
		    	Context initContext = new InitialContext();
		    	this.jndiSource = (DataSource) initContext.lookup(source);
		    	this.connection = this.jndiSource.getConnection();
		    if (!parameter.isEmpty()) {
		    		this.connection.prepareStatement("USE " + parameter).execute();
		    }
	    } catch (NamingException e) {
	    		throw new Merge500("Naming Exception: " + source);
	    } catch (SQLException e) {
	    		throw new Merge500("Error acquiring connection for " + source + ":" + e.getMessage());
	    }
	}
	
	@Override
	public ProviderMeta getMetaInfo() {
		return JndiProvider.meta;
	}
}

