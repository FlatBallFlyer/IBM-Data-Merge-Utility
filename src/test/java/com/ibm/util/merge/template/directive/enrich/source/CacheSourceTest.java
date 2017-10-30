package com.ibm.util.merge.template.directive.enrich.source;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.CacheProvider;

public class CacheSourceTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCacheSource() {
		CacheSource source = new CacheSource();
		assertEquals(AbstractSource.SOURCE_CACHE, source.getType());
	}

	@Test
	public void testGetProvider() throws MergeException {
		CacheSource source = new CacheSource();
		assertTrue(source.getProvider() instanceof CacheProvider);
	}

}
