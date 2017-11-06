package com.ibm.util.merge.template.directive.enrich.provider;

import java.util.HashMap;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;

public class CacheProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Merger context;
	private transient final DataProxyJson proxy = new DataProxyJson();
	
	public CacheProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
	}
	
	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) {
		TemplateCache cache = context.getCahce();
		Config config = context.getConfig();
		DataObject cacheData = new DataObject();
		cacheData.put("version", 		new DataPrimitive(Config.getVersion()));
		cacheData.put("runningSince", 	new DataPrimitive(cache.getInitialized().toString()));
		cacheData.put("CachedTemplates",new DataPrimitive(cache.getSize()));
		cacheData.put("CacheHits", 		new DataPrimitive(cache.getCacheHits()));
		cacheData.put("TempFolder", 	new DataPrimitive(config.getTempFolder()));
		cacheData.put("MaxRecursion", 	new DataPrimitive(config.getNestLimit()));
		cacheData.put("Statistics", 	proxy.fromJSON(proxy.toJson(cache.getStats()), DataObject.class));
		// DEFERRED: cacheData.put("totalMergeCount", cache.getTotalMergeCount());
		// DEFERRED: cacheData.put("mergeTime", cache.getMergeTime());
		// DEFERRED: cacheData.put("averageResposne", cache.getAverageResposne());
		return cacheData;
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
