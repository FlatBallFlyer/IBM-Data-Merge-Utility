package com.ibm.util.merge.directive;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderCsv;

public class ReplaceRowCsvTest extends ReplaceRowTest {

	@Before
	public void setUp() throws Exception {
		template = new Template();
		directive = new ReplaceRowCsv();

		ReplaceRowCsv myDirective = (ReplaceRowCsv) directive;
		template.addDirective(myDirective);
		ProviderCsv myProvider = (ProviderCsv) myDirective.getProvider();
		myProvider.setStaticData("A,B,C\n1,2,3");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloneTemplate() throws CloneNotSupportedException {
		ReplaceRowCsv newDirective = (ReplaceRowCsv) directive.clone();
		ReplaceRowCsv myDirective = (ReplaceRowCsv) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective();
		assertTrue(template.hasReplaceKey("{A}"));
		assertEquals("1",template.getReplaceValue("{A}"));
		assert(template.hasReplaceKey("{B}"));
		assertEquals("2",template.getReplaceValue("{B}"));
		assert(template.hasReplaceKey("{C}"));
		assertEquals("3",template.getReplaceValue("{C}"));
	}

}
