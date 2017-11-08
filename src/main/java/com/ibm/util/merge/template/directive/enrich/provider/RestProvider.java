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

import java.io.BufferedReader;

import org.apache.commons.io.IOUtils;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Simple HTTP Get based provider.
 * Environmental configuration format:
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
 * @author Mike Storey
 *
 */
public class RestProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Parser parser;
	private transient final Merger context;
	private transient final String username;
	private transient final String password;
	private transient final String host;
	private transient final String port;
	private transient final String url;
	
	public RestProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
		this.parser = new Parser();
		
		// Get Credentials
		String config = context.getConfig().getEnv(source);
		try {
			DataObject credentials = parser.parse(Parser.PARSE_JSON, config).getAsObject().get("credentials").getAsObject();
			this.username = credentials.get("username").getAsPrimitive();
			this.password = credentials.get("password").getAsPrimitive();
			this.host = 	credentials.get("uri_cli").getAsPrimitive();
			this.port = 	credentials.get("ca_certificate_base64").getAsPrimitive();
			this.url = 		credentials.get("uri").getAsPrimitive();
		} catch (MergeException e) {
			throw new Merge500("Invalid HTML Rest Provider for:" + source + "value: " + config);
		}
	}

	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws Merge500 {
		Content query = new Content(wrapper, command, TagSegment.ENCODE_HTML);
		query.replace(replace, false, context.getConfig().getNestLimit());
		String theUrl = "";
		String fetchedData = "";

		try {
			// TODO- Basic HTML Authentication (username password)
			theUrl = "http://" + this.getHost() + ":" + this.getPort() + "/" + query.getValue();
			fetchedData = IOUtils.toString(
					new BufferedReader(
					new InputStreamReader(
					new URL(theUrl).openStream())));
		} catch (MalformedURLException e) {
			throw new Merge500("Malformed URL:" + theUrl);
		} catch (IOException e) {
			throw new Merge500("I-O Exception at:" + theUrl);
		}

		return new DataPrimitive(fetchedData);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getUrl() {
		return url;
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
