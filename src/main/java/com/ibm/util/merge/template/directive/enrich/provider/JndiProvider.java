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
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * A JNDI based provider
 * <p>Environment Variable Format<blockquote><pre>
 * TODO
 * </pre></blockquote>
 * 
 * @author flatballflyer
 *
 */
public class JndiProvider extends SqlProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Option Name",
			"Credentials", 
			"Command Help",
			"Parse Help",
			"Return Help");
	
	class Credentials {
		public String db_type;
		public String name;
		public String uri_cli;
		public String ca_cert;
		public String deployment_id;
		public String uri;
	}

	private transient DataSource jndiSource = null;
	
	public JndiProvider(String source, String dbName, Merger context) throws MergeException {
		super(source, dbName, context);
	}

	@Override
	protected void connect() throws Merge500 {
    	try {
	    	Context initContext = new InitialContext();
	    	this.jndiSource = (DataSource) initContext.lookup(this.getSource());
	    	this.connection = this.jndiSource.getConnection();
	    } catch (NamingException e) {
	    	throw new Merge500("Naming Exception: " + this.getSource());
	    } catch (SQLException e) {
	    	throw new Merge500("Error acquiring connection for " + this.source + ":" + e.getMessage());
	    }
	}
	
	@Override
	public ProviderMeta getMetaInfo() {
		return JndiProvider.meta;
	}
}

