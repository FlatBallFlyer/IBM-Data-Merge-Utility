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
 * A MongoDb provider
 * <p>Environment Variable Format<blockquote><pre>
 *	<source>.URI
 *	<source>.USER
 *	<source>.PW
 *	<source>.DB
 * </pre></blockquote></p>
 * <p>
 * If USER is an empty string, Mongo Anonymous Auth is used, otherwise ScramSha1 authentication is used.
 * </p>
 * 
 * @author Mike Storey
 *
 */
public class MongoProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Collection",
			"ENV", 
			"Json Mongo Query Object",
			"Not Supported / Necessary",
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
	}
	
	private void connect() throws Merge500 {
		// Get Credentials
		String host = "";
		String port = "";
		String user = "";
		String pw = "";
		String dbName = "";
		try {
			host = Config.env(source + ".HOST");
			port = Config.env(source + ".PORT");
			user = Config.env(source + ".USER");
			pw = Config.env(source + ".PW");
			dbName = Config.env(source + ".DB");
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
		query.replace(replace, false, Config.nestLimit());
		DataObject queryObj = Config.parse(Config.PARSE_JSON, query.getValue()).getAsObject();

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
		
//		MongoCursor<Document> cursor = find.iterator();
//		while (cursor.hasNext()) {
//			Document aDoc = cursor.next();
//			DataElement theDoc = Config.parse(Config.PARSE_JSON, aDoc.toJson());
//			result.add(theDoc);
//		}
		
		if (find != null) {
			for (Document aDoc : find) {
				DataElement theDoc = Config.parse(Config.PARSE_JSON, aDoc.toJson());
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
