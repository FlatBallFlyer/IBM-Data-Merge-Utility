package com.ibm.util.merge.template.directive.enrich.provider;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;

public class CacheProvider extends AbstractProvider {
	
	public CacheProvider(AbstractSource source) throws MergeException {
		super(source);
		this.setType(PROVIDER_CACHE);
	}
	
	@Override
	public DataElement get(Template template) {
		TemplateCache cache = template.getContext().getCahce();
		Config config = cache.getConfig();
		DataObject cacheData = new DataObject();
		cacheData.put("version", 		new DataPrimitive(Merger.IDMU_VESION));
		cacheData.put("runningSince", 	new DataPrimitive(cache.getInitialized().toString()));
		cacheData.put("CachedTemplates",new DataPrimitive(cache.getSize()));
		cacheData.put("CacheHits", 		new DataPrimitive(cache.getCacheHits()));
		cacheData.put("TempFolder", 	new DataPrimitive(config.getTempFolder()));
		cacheData.put("MaxRecursion", 	new DataPrimitive(Config.MAX_NEST));
		cacheData.put("Statistics", 	proxy.fromJSON(proxy.toJson(cache.getStats()), DataObject.class));
		// DEFERRED: cacheData.put("totalMergeCount", cache.getTotalMergeCount());
		// DEFERRED: cacheData.put("mergeTime", cache.getMergeTime());
		// DEFERRED: cacheData.put("averageResposne", cache.getAverageResposne());
		DataList theResult = new DataList();
		theResult.add(cacheData);
		return theResult;
	}

	@Override
	public void put(Template template) {
		// NO-OP for this provider
	}

	@Override
	public void post(Template template) {
		// NO-OP for this provider
	}

	@Override
	public void delete(Template template) {
		// NO-OP for this provider
	}

}
