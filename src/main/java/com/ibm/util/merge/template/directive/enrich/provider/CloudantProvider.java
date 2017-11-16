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

import java.util.HashMap;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;

/**
 * Implements Cloudant Database support. Environment Variable for credentials
 *  
 * @author Mike Storey
 *
 */
public class CloudantProvider implements ProviderInterface {
	private final DataProxyJson proxy = new DataProxyJson();
	private static final ProviderMeta meta = new ProviderMeta(
			"Option Name",
			"Credentials", 
			"Command Help",
			"Parse Help",
			"Return Help");
	
	class Credentials {
		public String username;
		public String password;
		public String host;
		public String port;
		public String url;
	}

	private final String source;
	private final String dbName;
	private transient CloudantClient cloudant = null;
	private transient Database db = null;
	private transient final Merger context;
	
	/**
	 * Instantiate the provider and get the database connection
	 * @param source The Source Name
	 * @param dbName The DB Name
	 * @param context The Merge Context
	 * @throws MergeException on processing errors
	 */
	public CloudantProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}
	
	private void connect() throws MergeException {
		Credentials creds;
		try {
			creds = proxy.fromString(Config.env(source), Credentials.class);
		} catch (MergeException e) {
			throw new Merge500("Malformed or Missing Cloudant Source Credentials found for:" + source );
		}
		
		// Get the connection
		try {
			this.cloudant = ClientBuilder
					.account(creds.username)
					.username(creds.username)
					.password(creds.password)
					.build();
		} catch (CouchDbException e) {
			throw new Merge500("Unable to connect to Cloudant repository" + e.getMessage());
		}
		
		// Get the database object
		db = cloudant.database(this.getDbName(), true);
	}
	
	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		if (this.cloudant == null) {
			this.connect();
		}
		
		DataElement result = null;  
		
		String results = db.toString(); // TODO - Make Cloudant Call
		
		if (parseAs != Config.PARSE_NONE) {
			result = Config.parse(parseAs, results);
		}
		return result;
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

	@Override
	public ProviderMeta getMetaInfo() {
		return CloudantProvider.meta;
	}
	
}
