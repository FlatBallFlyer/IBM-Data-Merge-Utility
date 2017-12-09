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
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.Enrich;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>A Simple Anonymous HTTP Get based provider.</p>
 * <p>Provide Parameters usage</p>
 * <ul>
 * 		<li>String command - A Java RegEx expression used as a file name selector, can contain replace tags</li>
 * 		<li>int parseAs - Value from {@link com.ibm.util.merge.Config#PARSE_OPTIONS} If not NONE, contents are parsed, otherwise they are returned as a single primitive</li>
 * 		<li>Wrapper wrapper - Wrapper for tags in command</li>
 * 		<li>HashMap&lt;String,String&gt; replace - The Replace HashMap used to process tags in command</li>
 * 		<li>Merger context - Merger managing the merge</li>
 * </ul>
 * <p>Configuration Environment Variables</p>
 * <ul>
 * 		<li>{SourceName}.HOST - The Host</li>
 * 		<li>{SourceName}.PORT - The Port.</li>
 * 		<li>{SourceName}.USER - The HTTP User (not implemented).</li>
 * 		<li>{SourceName}.PW - The HTTP User PW (not implemented).</li>
 * </ul>
 * @see #RestProvider(String, String, Merger)    
 * @author Mike Storey
 *
 */
public class RestProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"N/A",
			"The following environment variables are expected\n"
			+ "{SourceName}.HOST - The Host\n"
			+ "{SourceName}.PORT - The Port\n",
			"The URL to make a http get request to",
			"Will parse the entire response",
			"Returns a Primitive if not parsed, or the Element from the parsing");
	
	public String username;
	public String password;
	public String host;
	public String port;
	
	/**
	 * Construct a provider
	 * @param source - Environment Variable prefix
	 * @param dbName - Not Applicable
	 * @param context - The Merger managing the provider
	 */
	public RestProvider() {
	}
	
	private void connect(Enrich context) throws Merge500 {
		String source = context.getEnrichSource();
		Config config = context.getConfig();
		try {
			host = config.getEnv(source + ".HOST");
			port = config.getEnv(source + ".PORT");
			username = config.getEnv(source + ".USER");
			password = config.getEnv(source + ".PW");
		} catch (MergeException e) {
			throw new Merge500("Rest Provider did not find environment variables:" + source + ":" + host + ":" + port + ":" + username + ":" + password);
		}
		// Add authentication code here and keep a secret
	}

	@Override
	public DataElement provide(Enrich context) throws MergeException {
		if (host == null) {
			connect(context);
		}
		
		Content query = new Content(context.getTemplate().getWrapper(), context.getEnrichCommand(), TagSegment.ENCODE_HTML);
		query.replace(context.getTemplate().getReplaceStack(), false, context.getConfig().getNestLimit());
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

		if (context.getParseAs() == Config.PARSE_NONE) {
			return new DataPrimitive(fetchedData);
		} else {
			return context.getConfig().parseString(context.getParseAs(), fetchedData);
		}
	}
	
	@Override
	public void close() {
		// nothing to close
		return;
	}
	
	@Override
	public ProviderMeta getMetaInfo() {
		return RestProvider.meta;
	}
	
}
