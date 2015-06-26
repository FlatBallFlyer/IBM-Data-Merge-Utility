/*
 * Copyright 2015, 2015 IBM
 * 
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
package com.ibm.util.merge.directive.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;

/**
 * @author Mike Storey
 *
 */
public abstract class ProviderHttp extends Provider implements Cloneable {
	private transient String fetchedData	= "";
	private String staticData	= "";
	private String url			= "";
	private String tag			= "";
	
	/**
	 * Simple Constructor
	 */
	public ProviderHttp() {
		super();
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderHttp clone() throws CloneNotSupportedException {
		return (ProviderHttp) super.clone();
	}

	/**
	 * Fetch the data
	 * 
	 * @throws MergeException Wrapped SQL and Process execptions
	 */
	@Override
	public void getData() throws MergeException {
		Template template = this.getDirective().getTemplate();
		if (!this.url.isEmpty()) {
			try {
				String theUrl = template.replaceProcess(this.url);
				this.fetchedData = IOUtils.toString(
						new BufferedReader(
						new InputStreamReader(
						new URL(theUrl).openStream())));
			} catch (MalformedURLException e) {
				throw new MergeException(e, "Malformed URL", this.url);
			} catch (IOException e) {
				throw new MergeException(e, "I-O Exception", this.url);
			}
		} else if (!this.tag.isEmpty()) {
			String key = Template.wrap(template.replaceProcess(this.tag));
			this.fetchedData = template.getReplaceValue(key);
		} else {
			this.fetchedData = this.staticData;
		}
	}
	
	/**
	 * The URL is the query context for this directive type.
	 */
	public String getQueryString() {
		return this.url;
	}

	public String getFetchedData() {
		return fetchedData;
	}

	public void setFetchedData(String fetchedData) {
		this.fetchedData = fetchedData;
	}

	public String getStaticData() {
		return staticData;
	}

	public void setStaticData(String staticData) {
		this.staticData = staticData;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}