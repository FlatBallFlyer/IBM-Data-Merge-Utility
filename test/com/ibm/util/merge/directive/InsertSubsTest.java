/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
