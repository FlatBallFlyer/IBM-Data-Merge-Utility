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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.ibm.util.merge.directive.provider.ProviderCsv;

public class ReplaceRowCsvTest extends ReplaceRowTest {
	private TemplateFactory tf;
	private ZipFactory zf;
	private ConnectionFactory cf;

	@Before
	public void setUp() throws Exception {
		tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates"));
		zf = new ZipFactory();
		cf = new ConnectionFactory();
		template = new Template();
		directive = new ReplaceRowCsv();

		ReplaceRowCsv myDirective = (ReplaceRowCsv) directive;
		template.addDirective(myDirective);
		ProviderCsv myProvider = (ProviderCsv) myDirective.getProvider();
		myProvider.setStaticData("A,B,C\n1,2,3");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCloneTemplate() throws CloneNotSupportedException {
		ReplaceRowCsv newDirective = (ReplaceRowCsv) directive.clone();
		ReplaceRowCsv myDirective = (ReplaceRowCsv) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective(tf, cf, zf);
		assertTrue(template.hasReplaceKey("{A}"));
		assertEquals("1",template.getReplaceValue("{A}"));
		assert(template.hasReplaceKey("{B}"));
		assertEquals("2",template.getReplaceValue("{B}"));
		assert(template.hasReplaceKey("{C}"));
		assertEquals("3",template.getReplaceValue("{C}"));
	}

	@Test
	public void testHasProvider() {
		assertEquals("com.ibm.util.merge.directive.provider.ProviderCsv", directive.getProvider().getClass().getName());
	}
}
