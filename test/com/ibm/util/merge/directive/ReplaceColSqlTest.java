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

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.Template;
import com.ibm.util.merge.directive.provider.*;

public class ReplaceColSqlTest extends ReplaceColTest {

	@Before
	public void setUp() throws Exception {
		provider = new ProviderStub();
		directive = new ReplaceColSql();
		ReplaceColSql myDirective = (ReplaceColSql) directive;
		myDirective.setProvider(provider);
		myDirective.setFromColumn("FromCol");
		myDirective.setToColumn("ToCol");

		template = new Template();
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneReplaceColSql() throws CloneNotSupportedException {
		ReplaceColSql newDirective = (ReplaceColSql) directive.clone();
		ReplaceColSql myDirective = (ReplaceColSql) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective();
		assertTrue(template.hasReplaceKey("{A}"));
		assertEquals("1",template.getReplaceValue("{A}"));
		assert(template.hasReplaceKey("{B}"));
		assertEquals("2",template.getReplaceValue("{B}"));
		assert(template.hasReplaceKey("{C}"));
		assertEquals("3",template.getReplaceValue("{C}"));
	}

	private class ProviderStub extends Provider {
		public ProviderStub() {
			super();
		}
		
		public ProviderStub clone() throws CloneNotSupportedException {
			ProviderStub provider = (ProviderStub) super.clone();
			return provider;
		}
		
		public void getData() throws MergeException {
			DataTable table = this.getNewTable();
			ArrayList<String> row = table.getNewRow();
			table.addCol("FromCol");table.addCol("ToCol");
			row.add("A");row.add("1");
			row = table.getNewRow();
			row.add("B");row.add("2");
			row = table.getNewRow();
			row.add("C");row.add("3");
		}

		public String getQueryString() {
			return "NA";
		}
	}
}
