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

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.TestUtils;
import com.ibm.util.merge.template.Template;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequireTest extends DirectiveTest {

	private MergeContext rtc;

	@Before
	public void setUp() throws Exception {
		rtc = TestUtils.createDefaultRuntimeContext();
		directive = new Require();
		Require myDirective = (Require) directive;
		myDirective.setTags("Tag1,Tag2");
		template = new Template();
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneRequire() throws CloneNotSupportedException {
		Require myDirective = (Require) directive;
		Require newDirective = new Require((Require)directive);
		assertNotEquals(directive, newDirective);
		assertEquals(myDirective.getTags(), newDirective.getTags());
	}

	@Test
	public void testExecuteDirectivePass() {
		template.addReplace("Tag1", "Test1");
		template.addReplace("Tag2", "Test1");

		try {
			directive.executeDirective(rtc);
		} catch (MergeException e) {
			fail("Pass Condition threw Exception");
		}
		assertNull(null);
	}

	@Test
	public void testExecuteDirectiveFail() {
		template.addReplace("Tag1", "Test1");

		try {
			directive.executeDirective(rtc);
		} catch (MergeException e) {
			assertNotNull(e);
			return;
		}
		fail("Not Found failed to throw exception");
	}

}
