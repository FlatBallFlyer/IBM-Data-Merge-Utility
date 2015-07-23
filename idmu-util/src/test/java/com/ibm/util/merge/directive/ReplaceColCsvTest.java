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
import com.ibm.util.merge.directive.provider.ProviderCsv;
import com.ibm.util.merge.template.Template;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReplaceColCsvTest extends ReplaceColTest {

	private MergeContext rtc;

	@Before
	public void setUp() throws Exception {
		rtc = TestUtils.createDefaultRuntimeContext();
		directive = new ReplaceColCsv();
		ReplaceColCsv myDirective = (ReplaceColCsv) directive;
		myDirective.setTemplate(template);
		myDirective.setFromColumn("A");
		myDirective.setToColumn("B");
		ProviderCsv myProvider = (ProviderCsv) myDirective.getProvider();
		myProvider.setStaticData("A,B,C\n1,2,3\n4,5,6\n");
		template = new Template();
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneReplaceColCsv() throws CloneNotSupportedException {
		ReplaceColCsv newDirective = new ReplaceColCsv((ReplaceColCsv)directive);
		ReplaceColCsv myDirective = (ReplaceColCsv) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective(rtc);
		assertTrue(template.hasReplaceKey("{1}"));
		assertEquals("2",template.getReplaceValue("{1}"));
		assert(template.hasReplaceKey("{4}"));
		assertEquals("5",template.getReplaceValue("{4}"));
	}

	@Test
	public void testHasProvider() {
		assertEquals("com.ibm.util.merge.directive.provider.ProviderCsv", directive.getProvider().getClass().getName());
	}
}
