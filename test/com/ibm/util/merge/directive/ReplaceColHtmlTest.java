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

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.ProviderHtml;

public class ReplaceColHtmlTest extends ReplaceColTest {

	@Before
	public void setUp() throws Exception {
		template = new Template();
		directive = new ReplaceColHtml();

		ReplaceColHtml myDirective = (ReplaceColHtml) directive;
		template.addDirective(myDirective);
		myDirective.setFromColumn("A");
		myDirective.setToColumn("B");
		ProviderHtml myProvider = (ProviderHtml) myDirective.getProvider();
		myProvider.setStaticData("<table><tr><th>A</th><th>B</th><th>C</th></tr><tr><td>1</td><td>2</td><td>3</td></tr></table>");
	}

	@Test
	public void testCloneReplaceColHtml() throws CloneNotSupportedException {
		ReplaceColHtml newDirective = (ReplaceColHtml) directive.clone();
		ReplaceColHtml myDirective = (ReplaceColHtml) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		// TODO - Testing of HTML Parsed Data
//		directive.executeDirective();
//		assertTrue(template.hasReplaceKey("{1}"));
//		assertEquals("2",template.getReplaceValue("{1}"));
	}

}
