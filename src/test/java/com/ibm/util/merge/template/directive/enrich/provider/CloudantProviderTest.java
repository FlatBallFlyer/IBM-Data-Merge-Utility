package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.provider.CloudantProvider;
import com.ibm.util.merge.template.directive.enrich.source.CloudantSource;

public class CloudantProviderTest {
	private CloudantSource source;
	private CloudantProvider provider;

	@Before
	public void setUp() throws Exception {
		source = new CloudantSource();
		provider = new CloudantProvider(source);
	}

	@Test
	public void testCloudantProvider() {
		assertEquals(AbstractProvider.PROVIDER_CLOUDANT, provider.getType());
	}
	
	@Test
	public void testGetObject() {
		// Requires Cloudant DB
	}

	@Test
	public void testGetTemplate() {
		// Requires Cloudant DB
	}

	@Test
	public void testGetSetDB() {
		// Requires Cloudant DB
	}

	@Test
	public void testGetSource() {
		assertTrue(provider.getSource() instanceof CloudantSource);
	}
}
