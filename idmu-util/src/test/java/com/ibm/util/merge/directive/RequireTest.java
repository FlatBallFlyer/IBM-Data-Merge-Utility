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
import com.ibm.util.merge.persistence.FilesystemPersistence;
import org.junit.Before;
import org.junit.Test;

public class RequireTest extends DirectiveTest {
	private TemplateFactory tf;
	private ZipFactory zf;
	private ConnectionFactory cf;

	@Before
	public void setUp() throws Exception {
		tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates"));
		zf = new ZipFactory();
		cf = new ConnectionFactory();
		directive = new Require();
		Require myDirective = (Require) directive;
		myDirective.setTags("Tag1,Tag2");
		template = new Template();
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneRequire() throws CloneNotSupportedException {
		Require myDirective = (Require) directive;
		Require newDirective = (Require) directive.clone();
		assertNotEquals(directive, newDirective);
		assertEquals(myDirective.getTags(), newDirective.getTags());
	}

	@Test
	public void testExecuteDirectivePass() {
		template.addReplace("Tag1", "Test1");
		template.addReplace("Tag2", "Test1");

		try {
			directive.executeDirective(tf, cf, zf);
		} catch (MergeException e) {
			fail("Pass Condition threw Exception");
		}
		assertNull(null);
	}

	@Test
	public void testExecuteDirectiveFail() {
		template.addReplace("Tag1", "Test1");

		try {
			directive.executeDirective(tf, cf, zf);
		} catch (MergeException e) {
			assertNotNull(e);
			return;
		}
		fail("Not Found failed to throw exception");
	}

}
