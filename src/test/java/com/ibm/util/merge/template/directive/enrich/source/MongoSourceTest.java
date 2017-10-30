package com.ibm.util.merge.template.directive.enrich.source;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.provider.MongoProvider;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.MongoSource;

public class MongoSourceTest {
	MongoSource source;
	
	@Before
	public void setup() {
		source = new MongoSource();
	}

	@Test
	public void testRestSource() {
		assertEquals(AbstractSource.SOURCE_MONGO, source.getType());
	}

	@Test
	public void testGetProviderVCAP() throws MergeException {
		AbstractProvider provider = source.getProvider();
		assertTrue(provider instanceof MongoProvider);
	}

	public void testGetSetDatabaseName() {
		source.setDatabaseName("Foo");
		assertEquals("Foo", source.getDatabaseName());
	}

}
