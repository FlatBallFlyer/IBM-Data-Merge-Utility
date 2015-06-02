package com.ibm.util.merge.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;

public class ReplaceValueTest extends DirectiveTest {
	
	@Before
	public void setUp() throws Exception {
		directive = new ReplaceValue();
		ReplaceValue myDirective = (ReplaceValue) directive;
		myDirective.setFrom("TestTag");
		myDirective.setTo("TestValue");

		template = new Template();
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneTemplate() throws CloneNotSupportedException {
		ReplaceValue myDirective = (ReplaceValue) directive;
		ReplaceValue newDirective = (ReplaceValue) directive.clone();
		assertNotEquals(directive, newDirective);
		assertEquals(myDirective.getFrom(), newDirective.getFrom());
		assertEquals(myDirective.getTo(), 	newDirective.getTo());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective();
		assertTrue(template.hasReplaceKey("{TestTag}"));
		assertEquals("TestValue", template.getReplaceValue("{TestTag}"));
	}

}
