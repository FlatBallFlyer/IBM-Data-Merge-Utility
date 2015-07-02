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

import com.ibm.util.merge.*;
import org.junit.Before;
import org.junit.Test;

public class ReplaceValueTest extends DirectiveTest {
	private TemplateFactory tf;
	private ZipFactory zf;
	private ConnectionFactory cf;

	@Before
	public void setUp() throws Exception {
		tf = new TemplateFactory();
		zf = new ZipFactory();
		cf = new ConnectionFactory();
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
		directive.executeDirective(tf, cf, zf);
		assertTrue(template.hasReplaceKey("{TestTag}"));
		assertEquals("TestValue", template.getReplaceValue("{TestTag}"));
	}

}
