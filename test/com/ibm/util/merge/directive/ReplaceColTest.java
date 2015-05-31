package com.ibm.util.merge.directive;

import static org.junit.Assert.*;
import org.junit.*;

public abstract class ReplaceColTest extends DirectiveTest {

	@Test
	public void testCloneReplaceCol() throws CloneNotSupportedException {
		ReplaceCol newDirective = (ReplaceCol) directive.clone();
		ReplaceCol myDirective = (ReplaceCol) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertEquals(myDirective.getFromColumn(), 	newDirective.getFromColumn());
		assertEquals(myDirective.getToColumn(), 	newDirective.getToColumn());
	}

}
