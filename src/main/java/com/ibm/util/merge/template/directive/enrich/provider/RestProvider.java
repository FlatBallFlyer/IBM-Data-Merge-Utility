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

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataPrimitive;
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
 * A Simple HTTP Get based provider.
 * <p>Environment Variable Format<blockquote><pre>
 * source.HOST
 * source.PORT
 * source.USER
 * source.PW
 * </pre></blockquote>
 *     
 * @author Mike Storey
 *
 */
public class RestProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"Option Name",
			"Credentials", 
			"Command Help",
			"Parse Help",
			"Return Help");
	
	public String username;
	public String password;
	public String host;
	public String port;

	private final String source;
	private final String dbName;
	private transient final Merger context;
	
	public RestProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}
	
	private void connect() throws Merge500 {
		try {
			host = Config.env(source + ".HOST");
			port = Config.env(source + ".PORT");
			username = Config.env(source + ".USER");
			password = Config.env(source + ".PW");
		} catch (MergeException e) {
			throw new Merge500("Rest Provider did not find environment variables:" + source + ":" + host + ":" + port + ":" + username + ":" + password);
		}
		// Add authentication code here and keep a secret
	}

	@Override
	public DataElement provide(String command, Wrapper wrapper, Merger context, HashMap<String,String> replace, int parseAs) throws MergeException {
		if (host == null) {
			connect();
		}
		
		Content query = new Content(wrapper, command, TagSegment.ENCODE_HTML);
		query.replace(replace, false, Config.nestLimit());
		String theUrl = "";
		String fetchedData = "";

		try {
			// TODO- Basic HTML Authentication (username password)
			theUrl = "http://" + this.host + ":" + this.port + "/" + query.getValue();
			fetchedData = IOUtils.toString(
					new BufferedReader(
					new InputStreamReader(
					new URL(theUrl).openStream())));
		} catch (MalformedURLException e) {
			throw new Merge500("Malformed URL:" + theUrl);
		} catch (IOException e) {
			throw new Merge500("I-O Exception at:" + theUrl);
		}

		if (parseAs == Config.PARSE_NONE) {
			return new DataPrimitive(fetchedData);
		} else {
			return Config.parse(parseAs, fetchedData);
		}
	}
	
	@Override
	public void close() {
		// nothing to close
		return;
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
		return RestProvider.meta;
	}
	
}
