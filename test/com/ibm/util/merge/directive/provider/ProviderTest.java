package com.ibm.util.merge.directive.provider;

import static org.junit.Assert.*;
import org.junit.*;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.Directive;

public abstract class ProviderTest {
	protected Provider provider;
	protected Directive directive;
	protected Template template;
	
	@Test
	public void testProviderCloneDirective() throws CloneNotSupportedException {
		Provider newProvider = provider.clone();
		assertNotEquals(provider, newProvider);
		assertEquals(0, newProvider.size());
		assertNull(newProvider.getDirective());
	}

	@Test
	public void testReset() {
		DataTable table = provider.reset();
		assertNotNull(table);
		assertEquals(1, provider.size());
	}

	@Test
	public void testGetTable() {
		assertNotNull(provider.getTable(0));
	}

	@Test
	public void testGetTableLessThanZero() {
		assertNull(provider.getTable(-1));
	}

	@Test
	public void testGetTableGreaterThanSize() {
		assertNull(provider.getTable(9));
	}

	@Test
	public void testGetNewTable() {
		DataTable newTable = provider.getNewTable();
		assertNotNull(newTable);
		assertEquals(2,provider.size());
	}
}
