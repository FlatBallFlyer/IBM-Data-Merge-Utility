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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.Directive;

/**
 * @author flatballflyer
 *
 */
public abstract class ProviderHttp extends Provider implements Cloneable {
	protected String fetchedData;
	protected String staticData;
	protected String url;
	protected String tag;
	
	/**
	 * @param newOwner
	 * @param dbRow
	 * @throws MergeException
	 */
	public ProviderHttp(Directive newOwner, ResultSet dbRow) throws MergeException {
		super(newOwner);
		try {
			this.staticData = dbRow.getString(Directive.COL_HTTP_STATIC_DATA);
			this.url 		= dbRow.getString(Directive.COL_HTTP_URL);
		} catch (SQLException e) {
			throw new MergeException(e, "ProviderHttp Construction SQL Error", this.getQueryString());
		}
	}
	
	/**
	 * Simple clone method
	 * @see com.ibm.util.merge.directive.provider.Provider#clone(com.ibm.util.merge.directive.Directive)
	 */
	public ProviderHttp clone(Directive newOwner) throws CloneNotSupportedException {
		return (ProviderHttp) super.clone(newOwner);
	}

	/**
	 * Fetch the data
	 * 
	 * @throws MergeException Wrapped SQL and Process execptions
	 */
	@Override
	public void getData() throws MergeException {
		if (this.url.isEmpty()) {
			this.fetchedData = this.staticData;
		} else {
			try {
				this.fetchedData = new URL(this.url).getContent().toString();
			} catch (MalformedURLException e) {
				throw new MergeException(e, "Malformed URL", this.url);
			} catch (IOException e) {
				throw new MergeException(e, "I-O Exception", this.url);
			}
		}
	}
	
	/**
	 * The URL is the query context for this directive type.
	 */
	public String getQueryString() {
		return this.url;
	}
}
