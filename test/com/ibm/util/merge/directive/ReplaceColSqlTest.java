package com.ibm.util.merge.directive;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderSql;

public class ReplaceColSqlTest extends ReplaceColTest {

	@Before
	public void setUp() throws Exception {
		template = new Template();
		directive = new ReplaceColSql();

		ReplaceColSql myDirective = (ReplaceColSql) directive;
		template.addDirective(myDirective);
		myDirective.setFromColumn("A");
		myDirective.setToColumn("B");
		ProviderSql myProvider = (ProviderSql) myDirective.getProvider();
		myProvider.setSource("TESTDB");
		myProvider.setColumns("*");
		myProvider.setFrom("TEST");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloneReplaceColSql() throws CloneNotSupportedException {
		ReplaceColSql newDirective = (ReplaceColSql) directive.clone();
		ReplaceColSql myDirective = (ReplaceColSql) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		// TODO - Requires Database Source
//		directive.executeDirective();
//		assertTrue(template.hasReplaceKey("{SomeValue}"));
//		assertEquals("SomeValue",template.getReplaceValue("{SomeValue}"));
	}

}
