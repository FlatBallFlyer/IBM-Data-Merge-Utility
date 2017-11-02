package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public class CacheProviderTest {
	private Config config;
	private TemplateCache cache;
	private Template template;
	private Merger context;
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		template = new Template("system", "test", "", "Content");
		cache.postTemplate(template);
		context = new Merger(cache, config, "system.test.");
	}

	@Test
	public void testCacheProvider() throws MergeException {
		CacheProvider provider = new CacheProvider("source", "db", context);
		assertEquals("source", provider.getSource());
		assertEquals("db", provider.getDbName());
		assertSame(context, provider.getContext());
	}

	@Test
	public void testProvide() throws MergeException {
		CacheProvider provider = new CacheProvider("source", "db", context);
		DataElement result = provider.provide("", template.getWrapper(), context, template.getReplaceStack());
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("version"));
		assertTrue(result.getAsObject().containsKey("runningSince"));
		assertTrue(result.getAsObject().containsKey("CachedTemplates"));
		assertTrue(result.getAsObject().containsKey("CacheHits"));
		assertTrue(result.getAsObject().containsKey("TempFolder"));
		assertTrue(result.getAsObject().containsKey("MaxRecursion"));
		assertTrue(result.getAsObject().containsKey("Statistics"));
	}

	@Test
	public void testGetSource() throws MergeException {
		CacheProvider provider = new CacheProvider("source", "db", context);
		assertEquals("source", provider.getSource());
	}

	@Test
	public void testGetDbName() throws MergeException {
		CacheProvider provider = new CacheProvider("source", "db", context);
		assertEquals("db", provider.getDbName());
	}

	@Test
	public void testGetContext() throws MergeException {
		CacheProvider provider = new CacheProvider("source", "db", context);
		assertSame(context, provider.getContext());
	}

}
