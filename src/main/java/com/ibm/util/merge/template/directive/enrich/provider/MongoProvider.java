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

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;

/**
 * A MongoDb provider
 * <p>Environment Variable Format<blockquote><pre>
 * TODO
 * </pre></blockquote>
 * 
 * @author Mike Storey
 *
 */
public class MongoProvider implements ProviderInterface {
	private final DataProxyJson proxy = new DataProxyJson();
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

	private final String source;
	private final String dbName;
	private transient final Merger context;
	private transient String connection = null; // TODO Mongo Connection type
	
	/**
	 * Instantiate the provider and get the db connection
	 * 
	 * @param source The Data Source
	 * @param dbName The DB Name
	 * @param context The Merge Context
	 * @throws MergeException  on processing errors
	 */
	public MongoProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}
	
	private void connect() throws Merge500 {
		Credentials creds;
		try {
			creds = proxy.fromString(Config.env(source), Credentials.class);
		} catch (MergeException e) {
			throw new Merge500("Invalid Mongo Provider for:" + source);
		}

		// TODO - Get Mongo Connection
		this.connection = creds.db_type + creds.name + creds.uri_cli + creds.ca_cert + creds.deployment_id + creds.uri;
	}

	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		if (connection == null) {
			this.connect();
		}
		
		DataElement result = null;
		Content query = new Content(wrapper, command, TagSegment.ENCODE_JSON);
		query.replace(replace, false, Config.nestLimit());
		
		String results = ""; // TODO - Make Mongo Call
		
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
		return MongoProvider.meta;
	}
}
