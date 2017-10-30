package com.ibm.util.merge.template.directive.enrich.source;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.CloudantProvider;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.CloudantSource;

public class CloudantSourceTest {
	CloudantSource source;

	@Before
	public void setup() {
		source = new CloudantSource();
	}
	
	@Test
	public void testCloudantSource() throws Merge500 {
		assertEquals(AbstractSource.SOURCE_CLOUDANT, source.getType());
	}

	@Test
	public void testGetNewProvider() throws MergeException {
		assertTrue(source.getProvider() instanceof CloudantProvider);
	}

	
	public void testGetSetDatabaseName() {
		source.setDatabaseName("Foo");
		assertEquals("Foo", source.getDatabaseName());
	}

}
