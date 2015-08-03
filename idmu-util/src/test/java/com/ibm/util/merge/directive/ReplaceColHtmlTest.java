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
//import com.ibm.util.merge.directive.provider.ProviderHtml;
import com.ibm.util.merge.template.Template;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
@Ignore
public class ReplaceColHtmlTest extends ReplaceColTest {

	private MergeContext rtc;

	@Before
	public void setUp() throws Exception {
//		rtc = TestUtils.createDefaultRuntimeContext();
//		template = new Template();
//		directive = new ReplaceColHtml();
//
//		ReplaceColHtml myDirective = (ReplaceColHtml) directive;
//		template.addDirective(myDirective);
//		myDirective.setFromColumn("A");
//		myDirective.setToColumn("B");
//		ProviderHtml myProvider = (ProviderHtml) myDirective.getProvider();
//		myProvider.setStaticData("<table><tr><th>A</th><th>B</th><th>C</th></tr><tr><td>1</td><td>2</td><td>3</td></tr><tr><td>4</td><td>5</td><td>6</td></tr></table>");
	}

	@Test
	public void testExecuteDirective() throws MergeException {
//		directive.executeDirective(rtc);
//		assertTrue(template.hasReplaceKey("{1}"));
//		assertEquals("2",template.getReplaceValue("{1}"));
//		assert(template.hasReplaceKey("{4}"));
//		assertEquals("5",template.getReplaceValue("{4}"));
	}

}
