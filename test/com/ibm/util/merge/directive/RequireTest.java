package com.ibm.util.merge.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;

public class RequireTest extends DirectiveTest {

	@Before
	public void setUp() throws Exception {
		directive = new Require();
		Require myDirective = (Require) directive;
		myDirective.setTags("Tag1,Tag2");
		template = new Template();
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneTemplate() throws CloneNotSupportedException {
		Require myDirective = (Require) directive;
		Require newDirective = (Require) directive.clone();
		assertNotEquals(directive, newDirective);
		assertEquals(myDirective.getTags(), newDirective.getTags());
	}

	@Test
	public void testExecuteDirectivePass() throws MergeException {
		// TODO - How to test exceptional conditions
//		directive.executeDirective();
//		assertTrue(template.hasReplaceKey("{TestTag}"));
//		assertEquals("TestValue", template.getReplaceValue("{TestTag}"));
	}

}
