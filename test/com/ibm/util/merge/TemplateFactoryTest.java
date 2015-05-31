package com.ibm.util.merge;

import static org.junit.Assert.*;
import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TemplateFactoryTest {
	private static String templateString1 = "[json:data]";
	private static String templateString2 = "[json:data]";
	private Template template1;
	private Template template2;
	
	@Before
	public void setUp() throws Exception {
		TemplateFactory.reset();
		template1 = TemplateFactory.cacheFromJson(templateString1);
		template2 = TemplateFactory.cacheFromJson(templateString2);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCacheFromJson() {
		assertEquals(2, TemplateFactory.size());
	}

	@Test
	public void testGetTemplateHttpServletRequest() {
		// TODO - How test this? (Can't instantiate ServletRequest artificially)
	}

	@Test
	public void testGetTemplateFullNameFoundInCache() throws MergeException {
		template2 = TemplateFactory.getTemplate(template1.getCollection(), template1.getName(), template1.getColumnValue(), new HashMap<String,String>());
		assertNotNull(template2);
		assertNotEquals(template1,template2);
		assertEquals(template1.getFullName(), template2.getFullName());
	}

	@Test
	public void testGetTemplateFullNameFoundInStore() throws MergeException {
		// TODO - Testing that requires persistence
		template2 = TemplateFactory.getTemplate(template1.getCollection(), template1.getName(), "Bar", new HashMap<String,String>());
		assertNotNull(template2);
		assertNotEquals(template1,template2);
		assertEquals(template1.getFullName(), template2.getFullName());
	}

	@Test
	public void testGetTemplateShortNameFoundInCache() throws MergeException {
		// TODO - Testing that requires persistence 
		template2 = TemplateFactory.getTemplate(template1.getCollection(), template1.getName(), "Foo", new HashMap<String,String>());
		assertNotNull(template2);
		assertNotEquals(template1,template2);
		assertEquals(template1.getFullName(), template2.getFullName());
	}

	@Test
	public void testGetTemplateShortNameFoundInStore() throws MergeException {
		// TODO - Testing that requires persistence 
		template2 = TemplateFactory.getTemplate(template1.getCollection(), template1.getName(), "Foo", new HashMap<String,String>());
		assertNotNull(template2);
		assertNotEquals(template1,template2);
		assertEquals(template1.getFullName(), template2.getFullName());
	}

	@Test
	public void testGetTemplateNotFound() throws MergeException {
		// TODO - How to test exceptional conditions?
		template2 = TemplateFactory.getTemplate(template1.getCollection(), template1.getName(), "Foo", new HashMap<String,String>());
		assertNotNull(template2);
		assertNotEquals(template1,template2);
		assertEquals(template1.getFullName(), template2.getFullName());
	}

	@Test
	public void testReset() {
		TemplateFactory.reset();
		assertEquals(0, TemplateFactory.size());
	}

	@Test
	public void testLoadFolder() {
		// TODO - Requires json files in "SomeTestFolder"
		TemplateFactory.loadFolder("SomeTestFolder");
		assertEquals(5, TemplateFactory.size());
	}

	@Test
	public void testSaveTemplate() throws MergeException {
		// TODO - Requires persistence
		assertNotNull(TemplateFactory.saveTemplate(templateString1));
	}

}
