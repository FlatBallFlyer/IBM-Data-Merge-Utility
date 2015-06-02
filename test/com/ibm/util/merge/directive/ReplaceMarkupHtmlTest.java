package com.ibm.util.merge.directive;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderHtml;

public class ReplaceMarkupHtmlTest extends DirectiveTest {

	@Before
	public void setUp() throws Exception {
		template = new Template();
		directive = new ReplaceRowHtml();

		ReplaceRowHtml myDirective = (ReplaceRowHtml) directive;
		template.addDirective(myDirective);
		ProviderHtml myProvider = (ProviderHtml) myDirective.getProvider();
		myProvider.setStaticData("<html><head></head><body></body></html>");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloneReplaceRowHtml() throws CloneNotSupportedException {
		ReplaceRowHtml newDirective = (ReplaceRowHtml) directive.clone();
		ReplaceRowHtml myDirective = (ReplaceRowHtml) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		// TODO - Requires HTML Parshing to be finished
//		directive.executeDirective();
//		assertTrue(template.hasReplaceKey("{SomeValue}"));
//		assertEquals("SomeDataValue",template.getReplaceValue("{SomeValue}"));
	}

}
