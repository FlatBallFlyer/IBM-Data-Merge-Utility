package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.provider.CacheProvider;
import com.ibm.util.merge.template.directive.enrich.source.CacheSource;

public class CacheProviderTest {
	Config config;
	TemplateCache cache;
	Merger context;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGet() throws MergeException {
		config = new Config();
		cache = new TemplateCache(config);
		context = new Merger(cache, config, "system.sample.");
		Template template = context.getBaseTemplate();
		
		CacheSource source = new CacheSource();
		CacheProvider provider = (CacheProvider) source.getProvider();
		DataElement result = provider.get(template);
		
		assertTrue(result.isList());
		assertTrue(result.getAsList().get(0).isObject());
		DataObject cache = result.getAsList().get(0).getAsObject();
		assertTrue(cache.containsKey("version"));
		assertTrue(cache.containsKey("runningSince"));
		assertTrue(cache.containsKey("CachedTemplates"));
		assertTrue(cache.containsKey("CacheHits"));
		assertTrue(cache.containsKey("PersistModel"));
		assertTrue(cache.containsKey("PersistSource"));
		assertTrue(cache.containsKey("TempFolder"));
		assertTrue(cache.containsKey("MaxRecursion"));
		assertTrue(cache.containsKey("Sources"));
		assertTrue(cache.containsKey("Statistics"));
	}

	@Test
	public void testPutPostDelete() throws MergeException {
		// NoOp for CacheProvider
	}

	@Test
	public void testPutMissing() throws MergeException {
		// NoOp for CacheProvider
	}

	@Test
	public void testPostDuplicate() throws MergeException {
		// NoOp for CacheProvider
	}

	@Test
	public void testDeleteMissing() throws MergeException {
		// NoOp for CacheProvider
	}

}
