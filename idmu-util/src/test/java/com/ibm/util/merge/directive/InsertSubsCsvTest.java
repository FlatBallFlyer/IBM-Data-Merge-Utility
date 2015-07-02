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

import java.util.HashMap;

import com.ibm.util.merge.ConnectionFactory;
import com.ibm.util.merge.ZipFactory;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.directive.provider.ProviderCsv;

public class InsertSubsCsvTest extends InsertSubsTest {
	private String subTemplate = "{\"collection\":\"root\",\"name\":\"sub\",\"content\":\"Row: {A}, Val: {B}\\n\"}";
	private String masterTemplate = "{\"collection\":\"root\",\"name\":\"master\",\"content\":\"Test \\u003ctkBookmark name\\u003d\\\"sub\\\" collection\\u003d\\\"root\\\"/\\u003e\"}";
	private String masterOutput= "Test Row: 1, Val: 2\nRow: 4, Val: 5\n<tkBookmark name=\"sub\" collection=\"root\"/>";
	private TemplateFactory tf;
	private ZipFactory zf;
	private ConnectionFactory cf;

	@Before
	public void setUp() throws Exception {
		tf = new TemplateFactory();
		zf = new ZipFactory();
		cf = new ConnectionFactory();
		directive = new InsertSubsCsv();
		InsertSubsCsv myDirective = (InsertSubsCsv) directive;
		ProviderCsv myProvider = (ProviderCsv) myDirective.getProvider();
		myProvider.setStaticData("A,B,C\n1,2,3\n4,5,6");
		
		tf.reset();
		tf.setDbPersistance(false);
		tf.cacheFromJson(subTemplate); 
		tf.cacheFromJson(masterTemplate);
		template = tf.getTemplate("root.master.", "", new HashMap<String,String>());
		template.addDirective(myDirective);
	}

	@Test
	public void testCloneInsertSubsCsv() throws CloneNotSupportedException {
		InsertSubsCsv newDirective = (InsertSubsCsv) directive.clone();
		InsertSubsCsv myDirective = (InsertSubsCsv) directive;
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

}
