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
package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class TemplateFactoryTest {
	private String template1 = "{\"collection\":\"root\",\"name\":\"test\",\"columnValue\":\"\"}";
	private String template2 = "{\"collection\":\"root\",\"name\":\"test\",\"columnValue\":\"foo\"}";
	private String template4 = "{\"collection\":\"foo\",\"columnValue\":\"foo\",\"name\":\"default\",\"description\":\"\",\"outputFile\":\"\",\"content\":\"Testing {Foo} Template \\u003ctkBookmark name\\u003d\\\"BKM1\\\"/ collection\\u003d\\\"COL1\\\"/\\u003e and {empty} \\u003ctkBookmark name\\u003d\\\"BKM2\\\" collection\\u003d\\\"COL2\\\"//\\u003e save to {folder}\",\"directives\":[" +
			 "{\"idTemplate\":22,\"sequence\":0,\"type\":1,\"softFail\":false,\"description\":\"Test Replace1      \",\"from\":\"Foo\",\"to\":\"Test Foo Value\"}" + 
 			",{\"idTemplate\":22,\"sequence\":1,\"type\":0,\"softFail\":false,\"description\":\"TestRequire        \",\"tags\":[\"Foo\",\"empty\"]}" + 
 			",{\"idTemplate\":22,\"sequence\":1,\"type\":2,\"softFail\":false,\"description\":\"TestInsertSubsTag  \",\"collectionName\":\"\",\"collectionColumn\":\"\",\"notLast\":[\"empty\"],\"onlyLast\":[],\"provider\":{\"tag\":\"Foo\",\"condition\":0,\"list\":false,\"value\":\"\"}}" + 
 			",{\"idTemplate\":22,\"sequence\":1,\"type\":21,\"softFail\":false,\"description\":\"TestInsertSubsCsv \",\"collectionName\":\"\",\"collectionColumn\":\"\",\"notLast\":[\"empty\"],\"onlyLast\":[],\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\"}}" +
			",{\"idTemplate\":22,\"sequence\":1,\"type\":10,\"softFail\":false,\"description\":\"TestInsertSubsSql \",\"collectionName\":\"\",\"collectionColumn\":\"\",\"notLast\":[\"empty\"],\"onlyLast\":[],\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\"}}" + 
			",{\"idTemplate\":22,\"sequence\":1,\"type\":31,\"softFail\":false,\"description\":\"TestInsertSubsHtml\",\"collectionName\":\"\",\"collectionColumn\":\"\",\"notLast\":[\"empty\"],\"onlyLast\":[],\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\"}}" + 
			",{\"idTemplate\":22,\"sequence\":1,\"type\":22,\"softFail\":false,\"description\":\"TestReplaceRowCsv \",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\"}}" + 
			",{\"idTemplate\":22,\"sequence\":1,\"type\":11,\"softFail\":false,\"description\":\"TestReplaceRowSql \",\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\"}}" + 
			",{\"idTemplate\":22,\"sequence\":1,\"type\":32,\"softFail\":false,\"description\":\"TestReplaceRowHtml\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\"}}" + 
			",{\"idTemplate\":22,\"sequence\":1,\"type\":23,\"softFail\":false,\"description\":\"TestReplaceColCsv \",\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\"}}" + 
			",{\"idTemplate\":22,\"sequence\":1,\"type\":12,\"softFail\":false,\"description\":\"TestReplaceColSql \",\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"provider\":{\"source\":\"\",\"columns\":\"A,B,C,1,2,3,4,5,6\",\"from\":\"\",\"where\":\"\"}}" + 
			",{\"idTemplate\":22,\"sequence\":1,\"type\":33,\"softFail\":false,\"description\":\"TestReplaceColHtml\",\"fromColumn\":\"Foo\",\"toColumn\":\"\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\"}}" + 
			",{\"idTemplate\":22,\"sequence\":1,\"type\":34,\"softFail\":false,\"description\":\"TestMarkupSubsHtml\",\"pattern\":\"TestPattern\",\"provider\":{\"staticData\":\"A,B,C\\n1,2,3\\n4,5,6\",\"url\":\"\",\"tag\":\"\"}}]}"; 
	@Before
	public void setUp() throws Exception {
		TemplateFactory.reset();
	}

	@Test
	public void testGetTemplateFullNameFoundInCache() throws MergeException {
		TemplateFactory.setDbPersistance(false);
		TemplateFactory.cacheFromJson(template1);
		TemplateFactory.cacheFromJson(template2);
		assertNotNull(TemplateFactory.getTemplate("root.test.foo", "", new HashMap<String,String>()));
	}

	@Test
	public void testGetTemplateFullNameFoundInDb() throws MergeException {
		// TODO Template Hibernate Testing
		TemplateFactory.setDbPersistance(false);
//		assertNotNull(TemplateFactory.getTemplate("root.test.found", "", new HashMap<String,String>()));
	}

	@Test
	public void testGetTemplateShortNameFoundInCache() throws MergeException {
		TemplateFactory.setDbPersistance(false);
		TemplateFactory.cacheFromJson(template1);
		Template template = TemplateFactory.getTemplate("root.test.nodata", "root.test.", new HashMap<String,String>());
		assertNotNull(template);
		assertEquals("root", template.getCollection());
		assertEquals("test", template.getName());
		assertEquals("", template.getColumnValue());
	}
	
	@Test
	public void testGetTemplateShortNameFoundInDb() throws MergeException {
		// TODO Template Hibernate Testing
		TemplateFactory.setDbPersistance(false);
//		assertNotNull(TemplateFactory.getTemplate("root.test.foo", "", new HashMap<String,String>()));
	}

	@Test
	public void testGetTemplateNotFoundInCache() throws MergeException {
		TemplateFactory.setDbPersistance(false);
		Template template = TemplateFactory.cacheFromJson(template1);
		assertNotNull(template);
		template = null;
		try {
			template = TemplateFactory.getTemplate("bad.template.test", "", new HashMap<String,String>());
		} catch (MergeException e) {
			assertNull(template);
			return;
		}
		fail("Template Not Found did not throw exception");
	}
	
	@Test
	public void testCacheFromJsonBasicParsing() throws MergeException {
		Template template = TemplateFactory.cacheFromJson(template1);
		assertEquals(1, TemplateFactory.size());
		assertEquals("root", template.getCollection());
		assertEquals("test", template.getName());
		assertEquals("", template.getColumnValue());
	}
	
	@Test
	public void testCacheFromJsonAllDirectiveParsing() throws MergeException {
		Template template = TemplateFactory.cacheFromJson(template4);
		assertEquals(1, TemplateFactory.size());
		assertEquals("foo", template.getCollection());
		assertEquals("default", template.getName());
		assertEquals(13, template.getDirectives().size());
		assertEquals("com.ibm.util.merge.directive.ReplaceValue", 		template.getDirectives().get(0).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.Require", 			template.getDirectives().get(1).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.InsertSubsTag", 		template.getDirectives().get(2).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.InsertSubsCsv", 		template.getDirectives().get(3).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.InsertSubsSql", 		template.getDirectives().get(4).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.InsertSubsHtml", 	template.getDirectives().get(5).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.ReplaceRowCsv", 		template.getDirectives().get(6).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.ReplaceRowSql", 		template.getDirectives().get(7).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.ReplaceRowHtml", 	template.getDirectives().get(8).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.ReplaceColCsv", 		template.getDirectives().get(9).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.ReplaceColSql", 		template.getDirectives().get(10).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.ReplaceColHtml", 	template.getDirectives().get(11).getClass().getName());
		assertEquals("com.ibm.util.merge.directive.ReplaceMarkupHtml", 	template.getDirectives().get(12).getClass().getName());
	}

	@Test
	public void testReset() throws MergeException {
		TemplateFactory.reset();
		TemplateFactory.cacheFromJson(template1);
		TemplateFactory.cacheFromJson(template2);
		assertEquals(2, TemplateFactory.size());
		TemplateFactory.reset();
		assertEquals(0, TemplateFactory.size());
	}


	@Test
	public void testLoadFolder() throws MergeException {
		TemplateFactory.setTemplateFolder("src/test/resources/templates/");
		TemplateFactory.loadAll();
		assertEquals(57, TemplateFactory.size()); 
	}

	@Test
	public void testGetTemplateAsJson() throws MergeException {
		TemplateFactory.reset();
		String template1 = TemplateFactory.cacheFromJson(template4).asJson(true);
		String template2 = TemplateFactory.getTemplateAsJson("foo.default.foo");
		assertEquals(template1, template2);
	}
	
	@Test
	public void testSaveTemplateFromJsonToFile() throws MergeException {
		TemplateFactory.reset();
		TemplateFactory.setDbPersistance(false);
		TemplateFactory.setTemplateFolder("src/test/resources/testout/");
		String template1 = TemplateFactory.saveTemplateFromJson(template4);
		Template template2 = TemplateFactory.cacheFromJson(template4);
		assertEquals(template1, template2.asJson(true));
	}

	@Test
	public void testSaveTemplateFromJsonToDb() throws MergeException {
		// TODO Template Hibernate Testing
		TemplateFactory.reset();
		TemplateFactory.setDbPersistance(false);
		TemplateFactory.setTemplateFolder("src/test/resources/testout/");
		String template1 = TemplateFactory.saveTemplateFromJson(template4);
		Template template2 = TemplateFactory.cacheFromJson(template4);
		assertEquals(template1, template2.asJson(true));
	}

	@Test
	public void testGetCollections() throws MergeException {
		TemplateFactory.reset();
		TemplateFactory.cacheFromJson(template1);
		TemplateFactory.cacheFromJson(template2);
		TemplateFactory.cacheFromJson(template4);
		String collections = TemplateFactory.getCollections();
		assertTrue(collections.contains("root"));
		assertTrue(collections.contains("foo"));
	}

	@Test
	public void testGetTemplates() throws MergeException {
		TemplateFactory.reset();
		TemplateFactory.cacheFromJson(template1);
		TemplateFactory.cacheFromJson(template2);
		TemplateFactory.cacheFromJson(template4);
		String templates = TemplateFactory.getTemplates("root");
		assertTrue(templates.contains("root.test."));
		assertTrue(templates.contains("root.test.foo"));
	}
}
