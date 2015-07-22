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

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.directive.AbstractDirective;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProviderSqlTest extends ProviderTest {

	@Before
	public void setUp() {
		provider = new ProviderSql();
		ProviderSql myProvider = (ProviderSql) provider;
		myProvider.reset();

		directive = new DirectiveStub();
		directive.setProvider(myProvider);
		
		template = new Template();
		template.addDirective(directive);
	}
	
	@After
	public void tearDown() {
		template = null;
		directive = null;
		provider = null;
	}
	
	@Test
	public void testProviderSqlCloneDirective() throws CloneNotSupportedException {
		ProviderSql myProvider = (ProviderSql) provider;
		ProviderSql newProvider = (ProviderSql) provider.clone();
		assertNotEquals(provider, newProvider);
		assertNull(newProvider.getDirective());
		assertEquals(myProvider.getColumns(), newProvider.getColumns());
		newProvider.setColumns("Foo");
		assertNotEquals(myProvider.getColumns(), newProvider.getColumns());
	}

	@Test
	public void testGetQueryString1() throws MergeException {
		ProviderSql myProvider = (ProviderSql) provider;
		myProvider.setColumns("COL1, COL2, {Foo}");
		myProvider.setFrom("");
		myProvider.setWhere("");
		template.addReplace("Foo", "Bar");
	
		assertEquals(myProvider.getQueryString(), "SELECT COL1, COL2, Bar");
	}

	@Test
	public void testGetQueryString2() throws MergeException {
		ProviderSql myProvider = (ProviderSql) provider;
		myProvider.setColumns("COL1, COL2");
		myProvider.setFrom("{Foo}, TABLE2");
		myProvider.setWhere("");
		template.addReplace("Foo", "Bar");
	
		assertEquals(myProvider.getQueryString(), "SELECT COL1, COL2 FROM Bar, TABLE2");
	}

	@Test
	public void testGetQueryString3() throws MergeException {
		ProviderSql myProvider = (ProviderSql) provider;
		myProvider.setColumns("COL1, COL2");
		myProvider.setFrom("TABLE1, TABLE2");
		myProvider.setWhere("TABLE1.ID='{Foo}'");
		template.addReplace("Foo", "Bar");
	
		assertEquals(myProvider.getQueryString(), "SELECT COL1, COL2 FROM TABLE1, TABLE2 WHERE TABLE1.ID='Bar'");
	}

	private class DirectiveStub extends AbstractDirective {
		public DirectiveStub() {}
		@Override
		public void executeDirective(MergeContext rtc) throws MergeException {}
	}
}
