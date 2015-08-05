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

import com.ibm.util.merge.MergeContext;
import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.directive.AbstractDirective;
import com.ibm.util.merge.template.Template;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProviderTagTest extends ProviderTest {
	//private String template1 = "{\"collection\":\"root\",\"name\":\"test\",\"columnValue\":\"\"}";
	private ProviderTag myProvider;
	DataTable table;

	@Before
	public void setUp() throws Exception {
		provider = new ProviderTag();
		provider.reset();
		myProvider = (ProviderTag) provider;

		directive = new DirectiveStub();
		directive.setProvider(provider);
		
		template = new Template();
		template.addDirective(directive);
	}

	@Test
	public void testGetDataExists() throws MergeException {
		template.addReplace("Foo", "Bar");
		myProvider.setCondition(ProviderTag.CONDITION_EXISTS);
		myProvider.setTag("Foo");

		assertEquals(1, myProvider.size());
		table = myProvider.getTable(0);
		assertNotNull(table);
		assertEquals(1, table.size());
		assertEquals("Bar", table.getValue(0, 0));
	}

	@Test
	public void testGetDataDoesNotExist() throws MergeException {
		myProvider.setCondition(ProviderTag.CONDITION_EXISTS);
		myProvider.setTag("Foo");
//		myProvider.getData(cf);

		assertEquals(1, myProvider.size());
		table = myProvider.getTable(0);
		assertEquals(0, table.size());
	}
	
	@Test
	public void testGetDataNonBlank() throws MergeException {
		template.addReplace("Foo", "Bar");
		myProvider.setCondition(ProviderTag.CONDITION_NONBLANK);
		myProvider.setTag("Foo");
//		myProvider.getData(cf);

		assertEquals(1, myProvider.size());
		table = myProvider.getTable(0);
		assertEquals(1, table.size());
		assertEquals("Bar", table.getValue(0, 0));
	}

	@Test
	public void testGetDataNonBlankMiss() throws MergeException {
		template.addReplace("Foo", "");
		myProvider.setCondition(ProviderTag.CONDITION_NONBLANK);
		myProvider.setTag("Foo");
//		myProvider.getData(cf);

		assertEquals(1, provider.size());
		table = provider.getTable(0);
		assertEquals(0, table.size());
	}

	@Test
	public void testGetDataIsBlank() throws MergeException {
		template.addReplace("Foo", "");
		myProvider.setCondition(ProviderTag.CONDITION_BLANK);
		myProvider.setTag("Foo");
//		myProvider.getData(cf);

		assertEquals(1, myProvider.size());
		table = myProvider.getTable(0);
		assertEquals(1, table.size());
		assertEquals("", table.getValue(0, 0));
	}

	@Test
	public void testGetDataIsBlankMiss() throws MergeException {
		template.addReplace("Foo", "Bar");
		myProvider.setCondition(ProviderTag.CONDITION_BLANK);
		myProvider.setTag("Foo");
//		myProvider.getData(cf);

		assertEquals(1, provider.size());
		table = provider.getTable(0);
		assertEquals(0, table.size());
	}

	@Test
	public void testGetDataEquals() throws MergeException {
		template.addReplace("Foo", "Bar");
		myProvider.setCondition(ProviderTag.CONDITION_EQUALS);
		myProvider.setTag("Foo");
		myProvider.setValue("Bar");
//		provider.getData(cf);

		assertEquals(1, provider.size());
		table = provider.getTable(0);
		assertNotNull(table);
		assertEquals(1, table.size());
		assertEquals("Bar", table.getValue(0, 0));
	}

	@Test
	public void testGetDataNotEquals() throws MergeException {
		template.addReplace("Foo", "Bar");
		myProvider.setCondition(ProviderTag.CONDITION_EQUALS);
		myProvider.setTag("Foo");
		myProvider.setValue("Miss");
//		provider.getData(cf);

		assertEquals(1, provider.size());
		table = provider.getTable(0);
		assertEquals(0, table.size());
	}

	@Test
	public void testGetDataList() throws MergeException {
		template.addReplace("Foo", "Bar,Fred,Wilma");
		myProvider.setCondition(ProviderTag.CONDITION_NONBLANK);
		myProvider.setTag("Foo");
		myProvider.setList(true);
//		provider.getData(cf);

		assertEquals(1, provider.size());
		table = provider.getTable(0);
		assertEquals(3, table.size());
		assertEquals("Bar",table.getValue(0, 0));
		assertEquals("Fred", table.getValue(1, 0));
		assertEquals("Wilma", table.getValue(2, 0));
	}

	private class DirectiveStub extends AbstractDirective {
		public DirectiveStub() {}
		@Override
		public void executeDirective(MergeContext rtc) throws MergeException {}
		@Override
		public AbstractDirective asNew() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public String getD1() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void setD1(String value) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public String getD2() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void setD2(String value) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public String getD3() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void setD3(String value) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public String getD4() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void setD4(String value) {
			// TODO Auto-generated method stub
			
		}
	}
}
