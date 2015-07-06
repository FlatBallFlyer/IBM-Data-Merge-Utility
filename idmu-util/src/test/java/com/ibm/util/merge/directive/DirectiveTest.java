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

import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.Provider;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class DirectiveTest {
	Template template;
	Directive directive;
	Provider provider;

	@Test
	public void testCloneDirective() throws CloneNotSupportedException {
		Directive newDirective = (Directive) directive.clone();
		assertNotEquals(directive, newDirective);
		assertNull(newDirective.getTemplate());
		assertEquals(directive.getDescription(), newDirective.getDescription());
		assertEquals(directive.getIdTemplate(),  newDirective.getIdTemplate());
		assertEquals(directive.getType(), 		 newDirective.getType());
		if (directive.getProvider() != null) {
			assertEquals(directive.getProvider().getClass().getName(), 
					  newDirective.getProvider().getClass().getName());
		}
	}
	
}
