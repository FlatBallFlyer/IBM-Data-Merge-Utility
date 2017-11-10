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
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.directive.enrich.source.CloudantClientMgr;

/**
 * Implements Cloudant Database support. Environment Variable for credentials 
 * will be 
 * 
 * "SOURCE_NAME": [
 *         {
 *             "credentials": {
 *                 "username": "",
 *                 "password": "",
 *                 "host": "",
 *                 "port": ,
 *                 "url": ""
 *             }
 *         }
 *     ],
 * 
 * @author flatballflyer
 *
 */
public class CloudantProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Merger context;
	private transient final DataProxyJson proxy = new DataProxyJson();
	private transient CloudantClient cloudant = null;
	private transient Database db = null;
	
	/**
	 * Instantiate the provider and get the database connection
	 * @param source
	 * @param dbName
	 * @param context
	 * @throws MergeException
	 */
	public CloudantProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
		String configString = "";
		String username;
		String password;
//		String host;
//		String port;
//		String url;
		
		// Fetch Credentials
		try {
			configString = Config.get().getEnv(source);
			DataObject credentials = proxy.fromJSON(configString, DataElement.class).getAsList().get(0).getAsObject().get("credentials").getAsObject();
			username = 	credentials.get("username").getAsPrimitive();
			password = 	credentials.get("password").getAsPrimitive();
//			host = 		credentials.get("host").getAsPrimitive();
//			port = 		credentials.get("port").getAsPrimitive();
//			url = 		credentials.get("url").getAsPrimitive();
		} catch (MergeException e){
			throw new Merge500("Malformed or Missing Cloudant Source Credentials found for:" + source + " for " + configString);
		}
		
		// Get the connection
		synchronized (CloudantClientMgr.class) {
			try {
				this.cloudant = ClientBuilder
						.account(username)
						.username(username)
						.password(password)
						.build();
			} catch (CouchDbException e) {
				throw new Merge500("Unable to connect to Cloudant repository" + e.getMessage());
			}
		} // end synchronized
		
		// Get the database object
		db = cloudant.database(this.getDbName(), true);
	}
	

	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace) {
		String result = "";  // TODO - Make Cloudant Call
		this.db.getClass();
		return new DataPrimitive(result);
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
}
