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

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.Enrich;

/**
 * Defines the Interface required by all Providers. See implementations for details
 * 
 * @author Mike Storey
 * @see #provide(Enrich context)
 */
public interface ProviderInterface {

	/**
	 * The meat of a provider - go get the data and put it in the DataManager
	 * 
	 * @param context - the Provider requesting data
	 * @return the DataElement that was fetched
	 * @throws MergeException when processing errors occur
	 */
	public abstract DataElement provide(Enrich context) throws MergeException;

	/**
	 * Close any connections
	 */
	public abstract void close();

	/**
	 * @return Provider Meta Data - describes specifics of provider behavior for end users
	 */
	public abstract ProviderMeta getMetaInfo();

}
