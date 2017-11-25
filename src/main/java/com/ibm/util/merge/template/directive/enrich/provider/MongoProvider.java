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

import org.bson.Document;
import org.bson.conversions.Bson;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * A MongoDb provider
 * <p>Environment Variable Format<blockquote><pre>
 * {
 * 		"uri" : "mongodb://localhost:27017",
 * 		"user" : "username",
 * 		"pword" : "some-password",
 * 		"dbName" : "some-db-name"
 * }
 * </pre></blockquote>
 * 
 * @author Mike Storey
 *
 */
public class MongoProvider implements ProviderInterface {
	private final DataProxyJson proxy = new DataProxyJson();
	private static final ProviderMeta meta = new ProviderMeta(
			"Collection",
			"{uri:uri,user:user,pword:pword,dbName:dbName}", 
			"Json Mongo Query Object",
			"Not Supported / Necessary",
			"List of Document Objects");
	
	class Credentials {
		public String uri;
		public String user;
		public String pword;
		public String dbName;
	}

	class Query extends HashMap<String,String> { 
	}

	private final String source;
	private final String collection;
	private transient final Merger context;
	private transient Credentials credentials = null;
	private transient MongoClient client;
	private transient MongoDatabase database;
	private transient MongoCollection<Document> dbCollection;
	
	/**
	 * Instantiate the provider and get the db connection
	 * 
	 * @param source The Data Source
	 * @param dbName The DB Name
	 * @param context The Merge Context
	 * @throws MergeException  on processing errors
	 */
	public MongoProvider(String source, String collection, Merger context) throws MergeException {
		this.source = source;
		this.collection = collection;
		this.context = context;
	}
	
	private void connect() throws Merge500 {
		try {
			this.credentials = proxy.fromString(Config.env(source), Credentials.class);
		} catch (MergeException e) {
			throw new Merge500("Invalid Mongo Provider for:" + source);
		}
		
		this.client = new MongoClient(new MongoClientURI(this.credentials.uri));
		this.database = this.client.getDatabase(this.credentials.dbName);
		this.dbCollection = database.getCollection(this.collection);
	}

	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		if (credentials == null) {
			this.connect();
		}
		
		Content query = new Content(wrapper, command, TagSegment.ENCODE_JSON);
		query.replace(replace, false, Config.nestLimit());
		DataObject queryObj = Config.parse(Config.PARSE_JSON, query.getValue()).getAsObject();

		DBObject dbquery = new BasicDBObject();
		for (String key : queryObj.keySet()) {
			dbquery.put(key, queryObj.get(key).getAsPrimitive());
		}
		FindIterable<Document> cursor = this.dbCollection.find((Bson) dbquery);
		
		DataList result = new DataList();
		for (Document aDoc : cursor) {
			DataElement theDoc = Config.parse(Config.PARSE_JSON, aDoc.toJson());
			result.add(theDoc);
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
