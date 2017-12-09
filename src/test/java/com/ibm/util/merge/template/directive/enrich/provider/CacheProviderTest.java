package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.Enrich;

public class CacheProviderTest {
	private Cache cache;
	private Template template;
	private Enrich directive;
	
	@Before
	public void setUp() throws Exception {
		cache = new Cache();
		template = new Template("system", "test", "", "Content");
		directive = new Enrich();
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger merger = new Merger(cache, "system.test.");
		template = template.getMergable(merger);
		directive = (Enrich) template.getDirectives().get(0);
	}

	@Test
	public void testCacheProvider() throws MergeException {
		CacheProvider provider = new CacheProvider("source", "parameter");
		assertNotNull(provider);
	}

	@Test
	public void testProvide() throws MergeException {
		CacheProvider provider = new CacheProvider("source", "parameter");
		DataElement result = provider.provide(directive);
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("version"));
		assertTrue(result.getAsObject().containsKey("runningSince"));
		assertTrue(result.getAsObject().containsKey("CachedTemplates"));
		assertTrue(result.getAsObject().containsKey("CacheHits"));
		assertTrue(result.getAsObject().containsKey("TempFolder"));
		assertTrue(result.getAsObject().containsKey("MaxRecursion"));
		assertTrue(result.getAsObject().containsKey("Statistics"));
	}

}
