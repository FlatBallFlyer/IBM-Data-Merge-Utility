package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class TemplateFactoryTest {
	private String template1 = "{\"collection\":\"root\",\"name\":\"test\",\"columnValue\":\"\"}";
	private String template2 = "{\"collection\":\"root\",\"name\":\"test\",\"columnValue\":\"foo\"}";
	
	@Before
	public void setUp() throws Exception {
		TemplateFactory.reset();
		// TODO - Requires Hibernate
		// TemplateFactory.initilizeHibernate();
		TemplateFactory.cacheFromJson(template1);
		TemplateFactory.cacheFromJson(template2);
	}

	@Test
	public void testCacheFromJson() {
		assertEquals(2, TemplateFactory.size());
	}

	@Test
	public void testReset() {
		TemplateFactory.reset();
		assertEquals(0, TemplateFactory.size());
	}

	@Test
	public void testGetTemplateHttpServletRequest() {
		// TODO - Requires HttpServletRequest
	}

	@Test
	public void testGetTemplateFullNameFoundInCache() throws MergeException {
		assertNotNull(TemplateFactory.getTemplate("root", "test", "foo", new HashMap<String,String>()));
	}

	@Test
	public void testGetTemplateFullNameFoundInStore() throws MergeException {
		// TODO - Testing that requires persistence
//		template = TemplateFactory.getTemplate("root", "test", "data", new HashMap<String,String>());
//		assertNotNull(template);
	}

	@Test
	public void testGetTemplateShortNameFoundInCache() throws MergeException {
		// TODO - Testing that requires persistence 
//		template = TemplateFactory.getTemplate("root", "test", "nodata", new HashMap<String,String>());
//		assertNotNull(template);
	}

	@Test
	public void testGetTemplateShortNameFoundInStore() throws MergeException {
		// TODO - Testing that requires persistence 
//		template = TemplateFactory.getTemplate("root", "default", "nodata", new HashMap<String,String>());
//		assertNotNull(template);
	}

	@Test
	public void testGetTemplateNotFound() throws MergeException {
		// TODO - How to test exceptional conditions?
//		template = TemplateFactory.getTemplate("foo", "bar", "bad", new HashMap<String,String>());
//		assertNull(template);
	}

	@Test
	public void testLoadFolder() {
		// TODO - Requires json files in "SomeTestFolder" 
//		TemplateFactory.loadFolder("SomeTestFolder");
//		assertEquals(5, TemplateFactory.size()); 
	}

	@Test
	public void testSaveTemplate() throws MergeException {
		// TODO - Requires persistence
		// TODO - assertNotNull(TemplateFactory.saveTemplate("JSON"));
	}

}
