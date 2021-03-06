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
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.Enrich;
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
 * 		<li>{SourceName}_URI - The database connection URL</li> 
 * 		<li>{SourceName}_USER - The database User ID, if empty Mongo Anonymous Auth is used, otherwise ScramSha1 authentication is used.</li>
 * 		<li>{SourceName}_PW - The database Password</li>
 * 		<li>{SourceName}_DB - The database name</li>
 * </ul>
 * @author Mike Storey
 *
 */
public class MongoProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Collection",
			"The following environment variables are expected\n"
			+ "{SourceName}_URI - The database connection URL\n"
			+ "{SourceName}_USER - The database User ID, if empty Mongo Anonymous Auth is used, otherwise ScramSha1 authentication is used.\n"
			+ "{SourceName}_PW - The database Password\n"
			+ "{SourceName}_DB - The database name", 
			"Json Mongo Query Object",
			"N/A",
			"List of Document Objects");
	
	class Query extends HashMap<String,String> {
		private static final long serialVersionUID = 1L; 
	}

	private transient MongoClient client;
	private transient MongoDatabase database;
	private transient MongoCollection<Document> dbCollection;
	private transient final String source;
	private transient final String parameter;
	
	/**
	 * Instantiate the provider and get the db connection
	 * @param source The mongo provider environment variable prefix
	 * @param parameter The mongo database name
	 */
	public MongoProvider(String source, String parameter) {
		this.source = source;
		this.parameter = parameter;
	}
	
	private void connect(Config config) throws Merge500 {
		// Implements lazy connection
		if (dbCollection != null) return;
		
		// Get Credentials
		String host = "";
		String port = "";
		String user = "";
		String pw = "";
		String dbName = "";
		try {
			host = config.getEnv(source + "_HOST");
			port = config.getEnv(source + "_PORT");
			user = config.getEnv(source + "_USER");
			pw = config.getEnv(source + "_PW");
			dbName = config.getEnv(source + "_DB");
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
		this.dbCollection = database.getCollection(parameter);
	}

	@Override
	public DataElement provide(Enrich context) throws MergeException {
		this.connect(context.getConfig());
		DataList result = new DataList();
		
		Content query = new Content(context.getTemplate().getWrapper(), context.getEnrichCommand(), TagSegment.ENCODE_JSON);
		query.replace(context.getTemplate().getReplaceStack(), false, context.getConfig().getNestLimit());
		DataObject queryObj = context.getConfig().parseString(
				Config.PARSE_JSON, 
				query.getValue(), 
				context.getParseOptions(), 
				context.getTemplate())
					.getAsObject();

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
				DataElement theDoc = context.getConfig().parseString(
						Config.PARSE_JSON, 
						aDoc.toJson(), 
						context.getParseOptions(), 
						context.getTemplate());
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
	public ProviderMeta getMetaInfo() {
		return MongoProvider.meta;
	}
}
