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
 * 		<li>{SourceName}_HOST - The Host</li>
 * 		<li>{SourceName}_PORT - The Port.</li>
 * 		<li>{SourceName}_USER - The HTTP User (not implemented).</li>
 * 		<li>{SourceName}_PW - The HTTP User PW (not implemented).</li>
 * </ul>
 * @author Mike Storey
 *
 */
public class RestProvider implements ProviderInterface {
	private static final ProviderMeta meta = new ProviderMeta(
			"N/A",
			"The following environment variables are expected\n"
			+ "{SourceName}_HOST - The Host\n"
			+ "{SourceName}_PORT - The Port\n",
			"The URL to make a http get request to",
			"Will parse the entire response",
			"Returns a Primitive if not parsed, or the Element from the parsing");
	
	public String username;
	public String password;
	public String host;
	public String port;
	private transient final String source;

	/**
	 * Construct a provider
	 * @param source The rest provider environment variable prefix
	 * @param parameter The mongo database name
	 */
	public RestProvider(String source, String parameter) {
		this.source = source;
	}
	
	private void connect(Config config) throws Merge500 {
		try {
			host = config.getEnv(source + "_HOST");
			port = config.getEnv(source + "_PORT");
//			username = config.getEnv(source + "_USER");
//			password = config.getEnv(source + "_PW");
		} catch (MergeException e) {
			throw new Merge500("Rest Provider did not find environment variables:" + source + ":" + host + ":" + port + ":" + username + ":" + password);
		}
		// Add authentication code here and keep a secret
	}

	@Override
	public DataElement provide(Enrich context) throws MergeException {
		connect(context.getConfig());
		
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
			return context.getConfig().parseString(
					context.getParseAs(), 
					fetchedData, 
					context.getParseOptions(), 
					context.getTemplate());
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
