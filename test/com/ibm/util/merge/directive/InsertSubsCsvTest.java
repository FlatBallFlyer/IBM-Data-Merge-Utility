package com.ibm.util.merge.directive;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderCsv;

public class InsertSubsCsvTest extends InsertSubsTest {

	@Before
	public void setUp() throws Exception {
		template = new Template();
		directive = new InsertSubsCsv();

		InsertSubsCsv myDirective = (InsertSubsCsv) directive;
		template.addDirective(myDirective);
		template.setContent(new StringBuilder("Some Template Content <tkBookmark name=\"foo\"/> and more content"));
		ProviderCsv myProvider = (ProviderCsv) myDirective.getProvider();
		myProvider.setStaticData("A,B,C\n1,2,3\n4,5,6");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloneInsertSubsCsv() throws CloneNotSupportedException {
		InsertSubsCsv newDirective = (InsertSubsCsv) directive.clone();
		InsertSubsCsv myDirective = (InsertSubsCsv) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective();
		assertTrue(template.getContent().contains("na"));
	}

}
