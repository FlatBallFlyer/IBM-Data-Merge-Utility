package com.ibm.util.merge.directive;

import static org.junit.Assert.*;
import org.junit.*;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderCsv;

public class ReplaceColCsvTest extends ReplaceColTest {

	@Before
	public void setUp() throws Exception {
		template = new Template();
		directive = new ReplaceColCsv();

		ReplaceColCsv myDirective = (ReplaceColCsv) directive;
		template.addDirective(myDirective);
		myDirective.setTemplate(template);
		myDirective.setFromColumn("A");
		myDirective.setToColumn("B");
		ProviderCsv myProvider = (ProviderCsv) myDirective.getProvider();
		myProvider.setStaticData("A,B,C\n1,2,3\n4,5,6\n");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloneReplaceColCsv() throws CloneNotSupportedException {
		ReplaceColCsv newDirective = (ReplaceColCsv) directive.clone();
		ReplaceColCsv myDirective = (ReplaceColCsv) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective();
		assertTrue(template.hasReplaceKey("{1}"));
		assertEquals("2",template.getReplaceValue("{1}"));
		assert(template.hasReplaceKey("{4}"));
		assertEquals("5",template.getReplaceValue("{4}"));
	}

}
