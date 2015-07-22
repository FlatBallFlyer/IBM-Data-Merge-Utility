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

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.directive.AbstractDirective;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Mike Storey
 *
 */
public abstract class ProviderHttp extends AbstractProvider implements Cloneable {
	private static final Logger log = Logger.getLogger(ProviderHttp.class.getName() );
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
	 * @see AbstractProvider#clone(AbstractDirective)
	 */
	@Override
	public ProviderHttp clone() throws CloneNotSupportedException {
		return (ProviderHttp) super.clone();
	}

	/**
	 * Fetch the data
	 * 
	 * @throws MergeException Wrapped SQL and Process execptions
	 * @param cf
	 */
	@Override
	public void getData(RuntimeContext rtc) throws MergeException {
		Template template = getDirective().getTemplate();
		if (!url.isEmpty()) {
			try {
				String theUrl = template.replaceProcess(url);
				fetchedData = IOUtils.toString(
						new BufferedReader(
						new InputStreamReader(
						new URL(theUrl).openStream())));
			} catch (MalformedURLException e) {
				throw new MergeException(this, e, "Malformed URL", url);
			} catch (IOException e) {
				throw new MergeException(this, e, "I-O Exception", url);
			}
		} else if (!tag.isEmpty()) {
			String key = Template.wrap(template.replaceProcess(tag));
			if (template.hasReplaceKey(key)) {
				fetchedData = template.getReplaceValue(key);
			} else {
				log.warn("Tag Data requuested but missing:" + key);
				if (!this.getDirective().isSoftFail()) {
					throw new MergeException(this, null, "Required Tag is not present:", key);
				}
			}
		} else {
			fetchedData = staticData;
		}
	}
	
	/**
	 * The URL is the query context for this directive type.
	 */
	@Override
	public String getQueryString() {
		if (!url.isEmpty()) return url;
		if (!tag.isEmpty()) return tag;
		return staticData;
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
