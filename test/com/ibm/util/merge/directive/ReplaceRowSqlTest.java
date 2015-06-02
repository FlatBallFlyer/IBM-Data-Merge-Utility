package com.ibm.util.merge.directive;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderSql;

public class ReplaceRowSqlTest extends ReplaceRowTest {

	@Before
	public void setUp() throws Exception {
		template = new Template();
		directive = new ReplaceRowSql();

		ReplaceRowSql myDirective = (ReplaceRowSql) directive;
		template.addDirective(myDirective);
		ProviderSql myProvider = (ProviderSql) myDirective.getProvider();
		myProvider.setSource("TESTDB");
		myProvider.setColumns("*");
		myProvider.setFrom("TEST");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloneReplaceRowHtml() throws CloneNotSupportedException {
		ReplaceRowSql newDirective = (ReplaceRowSql) directive.clone();
		ReplaceRowSql myDirective = (ReplaceRowSql) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		// TODO - Requires HTML Parshing to be finished
//		directive.executeDirective();
//		assertTrue(template.hasReplaceKey("{A}"));
//		assertEquals("1",template.getReplaceValue("{A}"));
//		assert(template.hasReplaceKey("{B}"));
//		assertEquals("2",template.getReplaceValue("{B}"));
//		assert(template.hasReplaceKey("{C}"));
//		assertEquals("3",template.getReplaceValue("{C}"));
	}

}
