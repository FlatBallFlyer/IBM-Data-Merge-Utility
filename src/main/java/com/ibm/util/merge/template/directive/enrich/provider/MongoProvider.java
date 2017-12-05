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

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * A MongoDb provider that gets data from a Mongo database
 * <p>Provide Parameters usage</p>
 * <ul>
 * 		<li>String command - A Mongo query JSON object, can contain replace tags</li>
 * 		<li>int parseAs - Not Applicable - Mongo data is always parsed as JSON</li>
 * 		<li>Wrapper wrapper - Wrapper for tags in command</li>
 * 		<li>HashMap&lt;String,String&gt; replace - The Replace HashMap used to process tags in command</li>
 * 		<li>Merger context - Merger managing the merge</li>
 * </ul>
 * <p>Configuration Environment Variables</p>
 * <ul>
 * 		<li>{SourceName}.URI - The database connection URL</li> 
 * 		<li>{SourceName}.USER - The database User ID, if empty Mongo Anonymous Auth is used, otherwise ScramSha1 authentication is used.</li>
 * 		<li>{SourceName}.PW - The database Password</li>
 * 		<li>{SourceName}.DB - The database name</li>
 * </ul>
 * @see #MongoProvider(String, String, Merger)
 * @author Mike Storey
 *
 */
public class MongoProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Collection",
			"The following environment variables are expected\n"
			+ "{SourceName}.URI - The database connection URL\n"
			+ "{SourceName}.USER - The database User ID, if empty Mongo Anonymous Auth is used, otherwise ScramSha1 authentication is used.\n"
			+ "{SourceName}.PW - The database Password\n"
			+ "{SourceName}.DB - The database name", 
			"Json Mongo Query Object",
			"N/A",
			"List of Document Objects");
	
	class Query extends HashMap<String,String> {
		private static final long serialVersionUID = 1L; 
	}

	private final String source;
	private final String collection;
	private transient final Merger context;
	private transient MongoClient client;
	private transient MongoDatabase database;
	private transient MongoCollection<Document> dbCollection;
	private transient final Config config;
	
	/**
	 * Instantiate the provider and get the db connection
	 * 
	 * @param source The Data Source
	 * @param collection The collection Name
	 * @param context The Merge Context
	 * @throws MergeException  on processing errors
	 */
	public MongoProvider(String source, String collection, Merger context) throws MergeException {
		this.source = source;
		this.collection = collection;
		this.context = context;
		this.config = context.getConfig();
	}
	
	private void connect() throws Merge500 {
		// Get Credentials
		String host = "";
		String port = "";
		String user = "";
		String pw = "";
		String dbName = "";
		try {
			host = this.config.getEnv(source + ".HOST");
			port = this.config.getEnv(source + ".PORT");
			user = this.config.getEnv(source + ".USER");
			pw = this.config.getEnv(source + ".PW");
			dbName = this.config.getEnv(source + ".DB");
		} catch (MergeException e) {
			throw new Merge500("Invalid Mongo Provider for:" + source);
		}
		
		ServerAddress addr = new ServerAddress(host, Integer.valueOf(port));
		if (user.isEmpty()) {
			this.client = new MongoClient(addr);
		} else {
			ArrayList<MongoCredential> creds = new ArrayList<MongoCredential>();
			MongoCredential credential = MongoCredential.createScramSha1Credential(user, dbName, pw.toCharArray());
			creds.add(credential);
			this.client = new MongoClient(addr, creds);
		}
		this.database = this.client.getDatabase(dbName);
		this.dbCollection = database.getCollection(this.collection);
	}

	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		DataList result = new DataList();
		if (this.dbCollection == null) {
			this.connect();
		}
		
		Content query = new Content(wrapper, command, TagSegment.ENCODE_JSON);
		query.replace(replace, false, this.config.getNestLimit());
		DataObject queryObj = this.config.parseString(Config.PARSE_JSON, query.getValue()).getAsObject();

		BasicDBObject dbquery = new BasicDBObject();
		for (String key : queryObj.keySet()) {
			dbquery.put(key, queryObj.get(key).getAsPrimitive());
		}
		FindIterable<Document> find = this.dbCollection.find(dbquery);
//		if (sort != null) {
//			find.sort(sort);
//		}
//		if (limit != null) {
//			find.limit(limit);
//		}
		
		if (find != null) {
			for (Document aDoc : find) {
				DataElement theDoc = this.config.parseString(Config.PARSE_JSON, aDoc.toJson());
				result.add(theDoc);
			}
		}
		return result;
	}

	@Override
	public void close() {
		if (this.client != null) {
			this.client.close();
		}
		return;
	}
		
	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public Merger getContext() {
		return this.context;
	}

	@Override
	public ProviderMeta getMetaInfo() {
		return MongoProvider.meta;
	}

	@Override
	public String getDbName() {
		return this.collection;
	}
}
