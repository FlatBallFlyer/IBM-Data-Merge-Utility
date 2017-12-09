package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Cache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.Enrich;

public class StubProviderTest {
	private Cache cache;
	private Template template;
	
	@Before
	public void setUp() throws Exception {
		cache = new Cache();
		template = new Template("system", "test", "", "Content");
		cache.postTemplate(template);
	}

	@Test
	public void testStubProvider() throws MergeException {
		StubProvider provider = new StubProvider("","");
		assertNotNull(provider);
	}

	@Test
	public void testProvide() throws MergeException {
		StubProvider provider = new StubProvider("","");
		DataElement result = provider.provide(new Enrich());
		assertTrue(result.isPrimitive());
	}
	
}
