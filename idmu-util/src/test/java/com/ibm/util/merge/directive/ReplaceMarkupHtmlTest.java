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

import com.ibm.util.merge.MergeException;
//import com.ibm.util.merge.directive.provider.ProviderHtml;
//import com.ibm.util.merge.template.Template;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Ignore
public class ReplaceMarkupHtmlTest extends DirectiveTest {

	@Before
	public void setUp() throws Exception {
//		template = new Template();
//		directive = new ReplaceRowHtml();
//
//		ReplaceRowHtml myDirective = (ReplaceRowHtml) directive;
//		template.addDirective(myDirective);
//		ProviderHtml myProvider = (ProviderHtml) myDirective.getProvider();
//		myProvider.setStaticData("<html><head></head><body></body></html>");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		// TODO - Test when HTML Markup implemented

	}

}
