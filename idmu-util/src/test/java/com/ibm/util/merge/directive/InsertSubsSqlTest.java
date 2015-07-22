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

import com.ibm.util.merge.*;
import com.ibm.util.merge.directive.provider.DataTable;
import com.ibm.util.merge.directive.provider.AbstractProvider;
import com.ibm.util.merge.json.DefaultJsonProxy;
import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.template.Template;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class InsertSubsSqlTest extends InsertSubsTest {
	private String subTemplate = "{\"collection\":\"root\",\"name\":\"sub\",\"content\":\"Row: {A}, Val: {B}\\n\"}";
	private String masterTemplate = "{\"collection\":\"root\",\"name\":\"master\",\"content\":\"Test \\u003ctkBookmark name\\u003d\\\"sub\\\" collection\\u003d\\\"root\\\"/\\u003e\"}";
	private String masterOutput= "Test Row: 1, Val: 2\nRow: 4, Val: 5\n<tkBookmark name=\"sub\" collection=\"root\"/>";

	private JsonProxy jsonProxy;
	private MergeContext rtc;

	@Before
	public void setUp() throws Exception {
		jsonProxy = new DefaultJsonProxy();
		rtc = TestUtils.createDefaultRuntimeContext();
		provider = new ProviderStub();
		directive = new InsertSubsSql();
		InsertSubsSql myDirective = (InsertSubsSql) directive;
		myDirective.setProvider(provider);
		TemplateFactory tf = rtc.getTemplateFactory();
		tf.reset();
		Template template2 = jsonProxy.fromJSON(subTemplate, Template.class);
		tf.cache(template2);
		Template template1 = jsonProxy.fromJSON(masterTemplate, Template.class);
		tf.cache(template1);
		template = tf.getTemplate("root.master.", "", new HashMap<>());
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneInsertSubslSql() throws CloneNotSupportedException {
		InsertSubsSql newDirective = (InsertSubsSql) directive.clone();
		InsertSubsSql myDirective = (InsertSubsSql) directive;
		assertNotEquals(myDirective, newDirective);
		assertNull(newDirective.getTemplate());
		assertNotEquals(myDirective.getProvider(), newDirective.getProvider());
		assertEquals(0, newDirective.getProvider().size());
	}

	@Test
	public void testExecuteDirective() throws MergeException {
		directive.executeDirective(rtc);
		assertEquals(masterOutput, template.getContent());
	}

	private class ProviderStub extends AbstractProvider {
		public ProviderStub() {
			super();
		}
		
		@Override
		public ProviderStub clone() throws CloneNotSupportedException {
			ProviderStub provider = (ProviderStub) super.clone();
			return provider;
		}
		
		@Override
		public void getData(MergeContext ctx) throws MergeException {
			DataTable table = addNewTable();
			ArrayList<String> row;
			table.addCol("A");table.addCol("B");table.addCol("C");
			row = table.addNewRow();
			row.add("1");row.add("2");row.add("3");
			row = table.addNewRow();
			row.add("4");row.add("5");row.add("6");
		}

		@Override
		public String getQueryString() {
			return "NA";
		}
	}
}
