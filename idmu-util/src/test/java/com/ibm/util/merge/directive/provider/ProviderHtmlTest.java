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
package com.ibm.util.merge.directive.provider;

import static org.junit.Assert.*;

import com.ibm.util.merge.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.ibm.util.merge.directive.Directive;

public class ProviderHtmlTest extends ProviderHttpTest {
	
	@Before
	public void setUp() {
		template = new Template();
		directive = new DirectiveStub();
		provider = new ProviderHtml();

		ProviderHttp myProvider = (ProviderHttp) provider;
		directive.setProvider(myProvider);
		template.addDirective(directive);
		template.addReplace("TEST_TAG", "<html><head></head><body><table><tr><th>A</th><th>B</th><th>C</th></tr><tr><td>1</td><td>2</td><td>3</td></tr><tr><td>4</td><td>5</td><td>6</td></tr></table></body></html>");
		myProvider.reset();
		myProvider.setTag("TEST_TAG");
		myProvider.setStaticData("<html><head></head><body><table><tr><th>X</th><th>Y</th><th>Z</th></tr><tr><td>1</td><td>2</td><td>3</td></tr><tr><td>4</td><td>5</td><td>6</td></tr></table></body></html>");
		myProvider.setUrl("http://sometestdata.csv");
	}
	
	@After
	public void tearDown() {
		template = null;
		directive = null;
		provider = null;
	}
	
	@Test
	public void testGetDataFromTag() throws MergeException {
		super.testGetDataFromTag();
		assertEquals(1,provider.size());
		assertEquals(2,provider.getTable(0).size());
		assertEquals("1", provider.getTable(0).getValue(0, "A"));
		assertEquals("6", provider.getTable(0).getValue(1, "C"));
	}

	@Test
	public void testGetDataFromStatic() throws MergeException {
		super.testGetDataFromStatic();
		assertEquals(1,provider.size());
		assertEquals(2,provider.getTable(0).size());
		assertEquals("1", provider.getTable(0).getValue(0,  "X"));
		assertEquals("6", provider.getTable(0).getValue(1,  "Z"));
	}

	@Test
	public void testProviderHtmlCloneDirective() throws CloneNotSupportedException {
		ProviderHtml newProvider = (ProviderHtml) provider.clone();
		assertNotEquals(provider, newProvider);
		assertNull(newProvider.getDirective());
	}

	@Test
	public void testGetQueryString() {
		assertEquals("http://sometestdata.csv", provider.getQueryString());
	}

	private class DirectiveStub extends Directive {
		public DirectiveStub() {}
		public void executeDirective(TemplateFactory tf, ConnectionFactory cf, ZipFactory zf) throws MergeException {}
	}
}
