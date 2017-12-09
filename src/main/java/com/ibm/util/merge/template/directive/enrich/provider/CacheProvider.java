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

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.Enrich;

/**
 * <p>Provide information about the template cache (for Status templates). Always returns an object with a summary of Cache info</p>
 * <p>Provide Parameters usage</p>
 * <ul>
 * 		<li>String command - Not Applicable</li>
 * 		<li>int parseAs - Not Applicable
 * 		<li>Wrapper wrapper - Not Applicable</li>
 * 		<li>HashMap&lt;String,String&gt; replace - Not Applicable</li>
 * 		<li>Merger context - Merger managing the merge</li>
 * </ul>
 * <p>Configuration Environment Variables</p>
 * <ul>
 * 		<li>None</li> 
 * </ul>
 * 
 * @author Mike Storey
 * @since 4.0.0
 *
 */
public class CacheProvider implements ProviderInterface {
	private transient final DataProxyJson proxy = new DataProxyJson();
	
	/**
	 * Instantiate a Cache Provider
	 * @param source The source name (ignored)
	 * @param dbName The dbName (ignored)
	 * @param context The Merge Context
	 * @throws MergeException Never
	 */
	public CacheProvider() throws MergeException {
	}
	
	@Override
	public DataElement provide(Enrich context) throws MergeException {
		Cache cache = context.getContext().getCahce();
		DataObject cacheData = new DataObject();
		Config config = context.getConfig();
		cacheData.put("version", 		new DataPrimitive(config.getVersion()));
		cacheData.put("runningSince", 	new DataPrimitive(cache.getInitialized().toString()));
		cacheData.put("CachedTemplates",new DataPrimitive(cache.getSize()));
		cacheData.put("CacheHits", 		new DataPrimitive(cache.getCacheHits()));
		cacheData.put("TempFolder", 	new DataPrimitive(config.getTempFolder()));
		cacheData.put("MaxRecursion", 	new DataPrimitive(config.getNestLimit()));
		cacheData.put("Statistics", 	proxy.fromString(proxy.toString(cache.getStats()), DataObject.class));
		// DEFERRED: cacheData.put("totalMergeCount", cache.getTotalMergeCount());
		// DEFERRED: cacheData.put("mergeTime", cache.getMergeTime());
		// DEFERRED: cacheData.put("averageResposne", cache.getAverageResposne());
		return cacheData;
	}

	@Override
	public void close() {
		// nothing to close
		return;
	}
	
	@Override
	public ProviderMeta getMetaInfo() {
		return new ProviderMeta(
				"No Options are Supported",
				"No further configuration needed",
				"No Command is needed",
				"No Parsing is supported",
				"returns an object of <String, Primitive> with cache statistics");
	}
	
}
