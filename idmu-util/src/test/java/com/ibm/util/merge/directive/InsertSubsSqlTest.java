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
import java.util.HashMap;

import com.ibm.util.merge.*;
import org.junit.Before;
import org.junit.Test;
import com.ibm.util.merge.directive.provider.*;

public class InsertSubsSqlTest extends InsertSubsTest {
	private String subTemplate = "{\"collection\":\"root\",\"name\":\"sub\",\"content\":\"Row: {A}, Val: {B}\\n\"}";
	private String masterTemplate = "{\"collection\":\"root\",\"name\":\"master\",\"content\":\"Test \\u003ctkBookmark name\\u003d\\\"sub\\\" collection\\u003d\\\"root\\\"/\\u003e\"}";
	private String masterOutput= "Test Row: 1, Val: 2\nRow: 4, Val: 5\n<tkBookmark name=\"sub\" collection=\"root\"/>";
	private TemplateFactory tf;
	private ZipFactory zf;
	private ConnectionFactory cf;

	@Before
	public void setUp() throws Exception {
		tf = new TemplateFactory(new FilesystemPersistence("/home/spectre/Projects/IBM/IBM-Data-Merge-Utility/idmu-war/src/main/webapp/WEB-INF/templates"));
		zf = new ZipFactory();
		cf = new ConnectionFactory();
		provider = new ProviderStub();
		directive = new InsertSubsSql();
		InsertSubsSql myDirective = (InsertSubsSql) directive;
		myDirective.setProvider(provider);

		tf.reset();

		tf.cacheFromJson(subTemplate); 
		tf.cacheFromJson(masterTemplate);
		template = tf.getTemplate("root.master.", "", new HashMap<String,String>());
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
		directive.executeDirective(tf, cf, zf);
		assertEquals(masterOutput, template.getContent());
	}

	private class ProviderStub extends Provider {
		public ProviderStub() {
			super();
		}
		
		public ProviderStub clone() throws CloneNotSupportedException {
			ProviderStub provider = (ProviderStub) super.clone();
			return provider;
		}
		
		public void getData(ConnectionFactory cf) throws MergeException {
			DataTable table = this.getNewTable();
			ArrayList<String> row;
			table.addCol("A");table.addCol("B");table.addCol("C");
			row = table.getNewRow();
			row.add("1");row.add("2");row.add("3");
			row = table.getNewRow();
			row.add("4");row.add("5");row.add("6");
		}

		public String getQueryString() {
			return "NA";
		}
	}
}
