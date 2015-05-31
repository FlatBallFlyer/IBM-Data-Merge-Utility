package com.ibm.util.merge.directive;

import static org.junit.Assert.*;
import org.junit.*;

public abstract class InsertSubsTest extends DirectiveTest {

	@Test
	public void testCloneInsertSubs() throws CloneNotSupportedException {
		InsertSubs newDirective = (InsertSubs) directive.clone();
		InsertSubs myDirective = (InsertSubs) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertEquals(myDirective.getCollectionColumn(), 	newDirective.getCollectionColumn());
		assertEquals(myDirective.getCollectionName(), 		newDirective.getCollectionName());
		assertEquals(myDirective.getNotLast(), 				newDirective.getNotLast());
		assertEquals(myDirective.getOnlyLast(),				newDirective.getOnlyLast());
	}

	@Test
	public void testSetNotLast() {
		InsertSubs myDirective = (InsertSubs) directive;
		myDirective.setNotLast("One,Two,Three,Four");
		assertEquals("One,Two,Three,Four", myDirective.getNotLast());
	}

	@Test
	public void testSetOnlyLast() {
		InsertSubs myDirective = (InsertSubs) directive;
		myDirective.setOnlyLast("One,Two,Three,Four");
		assertEquals("One,Two,Three,Four", myDirective.getOnlyLast());
	}

}
