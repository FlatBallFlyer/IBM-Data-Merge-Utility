package com.ibm.util.merge.directive;

import static org.junit.Assert.*;
import org.junit.*;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.Provider;

public abstract class DirectiveTest {
	Template template;
	Directive directive;
	Provider provider;

	@Test
	public void testCloneDirective() throws CloneNotSupportedException {
		Directive newDirective = (Directive) directive.clone();
		assertNotEquals(directive, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(directive.getProvider(), newDirective.getProvider());
		assertEquals(directive.getDescription(), newDirective.getDescription());
		assertEquals(directive.getIdDirective(), newDirective.getIdDirective());
		assertEquals(directive.getIdTemplate(),  newDirective.getIdTemplate());
		assertEquals(directive.getType(), 		 newDirective.getType());
	}

}
