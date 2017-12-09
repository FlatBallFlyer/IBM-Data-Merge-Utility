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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.Enrich;

/**
 * <p>A Cloudant provider that fetches documents from a Cloudant Database</p>
 * <p>Provide Parameters usage</p>
 * <ul>
 * 		<li>String command - A Cloudant query JSON object, can contain replace tags</li>
 * 		<li>int parseAs - Not Applicable
 * 		<li>Wrapper wrapper - Wrapper for tags in command</li>
 * 		<li>HashMap&lt;String,String&gt; replace - The Replace HashMap used to process tags in command</li>
 * 		<li>Merger context - Merger managing the merge</li>
 * </ul>
 * <p>Configuration Environment Variables</p>
 * <ul>
 * 		<li>{SourceName}.URL - The database connection URL</li> 
 * 		<li>{SourceName}.USER - The database User ID</li>
 * 		<li>{SourceName}.PW - The database Password</li>
 * </ul>
 * @see #CloudantProvider(String source, String dbName, Merger context)
 *   
 * @author Mike Storey
 */
public class CloudantProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Database",
			"The following environment variables are expected: /n"
			+ "{SourceName}.URL - The database connection URL/n"
			+ "{SourceName}.USER - The database User ID/n"
			+ "{SourceName}.PW - The database Password", 
			"A cloudant Query JSON string - Replace Tags are supported and jSon encoded",
			"N/A",
			"This provider always returns a List of Objects");
	
	private transient CloudantClient cloudant = null;
	private transient Database db = null;
	private transient DataProxyJson proxy = new DataProxyJson();
	
	/**
	 * Instantiate the provider and get the database connection
	 * @param source The Source Name
	 * @param dbName The DB Name
	 * @param context The Merge Context
	 * @throws MergeException on processing errors
	 */
	public CloudantProvider() throws MergeException {
	}
	
	private void connect(Enrich context) throws MergeException {

		// Get the credentials
		String user = "";
		String pw = "";
		String url = "";
		String source = "";
		Config config = context.getConfig();
		try {
			source = context.getEnrichSource();
			url = config.getEnv(source + ".URL");
			user = config.getEnv(source + ".USER");
			pw = config.getEnv(source + ".PW");
		} catch (MergeException e) {
			throw new Merge500("Malformed or Missing Cloudant Source Credentials found for:" + source + ":" + url + ":" + user + ":" + pw);
		}
		
		try {
			// Get the connection
			this.cloudant = ClientBuilder
					.url(new URL(url))
					.username(user)
					.password(pw)
					.gsonBuilder(proxy.getBuilder())
					.build();
			// Get the database object
			db = cloudant.database(context.getEnrichParameter(), false);
		} catch (CouchDbException e) {
			throw new Merge500("Unable to connect to Cloudant repository:" + url + ":" + user + ":" + pw + ":" + context.getEnrichParameter() + ":" + e.getMessage());
		} catch (MalformedURLException e) {
			throw new Merge500("Invalid URL for provider :" + context.getEnrichSource() + ":" + context.getEnrichParameter() + "Message:" + e.getMessage());
		}
		
	}
	
	@Override
	public DataElement provide(Enrich context) throws MergeException {
		if (this.cloudant == null) {
			this.connect(context);
		}

		Content query = new Content(context.getTemplate().getWrapper(), context.getEnrichCommand(), TagSegment.ENCODE_JSON);
		query.replace(context.getTemplate().getReplaceStack(), false, context.getConfig().getNestLimit());
		
		List<DataElement> results;
		
		results = db.findByIndex(query.getValue(), DataElement.class);
		
		DataList reply = new DataList();
		for (DataElement doc : results) {
			reply.add(doc);
		}
		return reply;
	}
	
	@Override
	public void close() {
		// Nothing to do - I think..... need Cloudant Help
		return;
	}
	
	@Override
	public ProviderMeta getMetaInfo() {
		return CloudantProvider.meta;
	}
	
}
