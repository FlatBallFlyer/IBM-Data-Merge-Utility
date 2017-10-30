package com.ibm.util.merge.template;

import static org.junit.Assert.*;

import org.junit.Test;

public class TemplateIdTest {

	@Test
	public void testTemplateIdStringStringString() {
		TemplateId id = new TemplateId("system","test","vary");
		assertEquals("system",id.group);
		assertEquals("test",id.name);
		assertEquals("vary",id.variant);
	}

	@Test
	public void testTemplateIdStringStringStringEmpty() {
		TemplateId id = new TemplateId("","","");
		assertTrue(id.group.isEmpty());
		assertTrue(id.name.isEmpty());
		assertTrue(id.variant.isEmpty());
	}

	@Test
	public void testTemplateIdString1() {
		TemplateId id = new TemplateId("");
		assertTrue(id.group.isEmpty());
		assertTrue(id.name.isEmpty());
		assertTrue(id.variant.isEmpty());
	}

	@Test
	public void testTemplateIdString2() {
		TemplateId id = new TemplateId("system");
		assertEquals("system",id.group);
		assertTrue(id.name.isEmpty());
		assertTrue(id.variant.isEmpty());
	}

	@Test
	public void testTemplateIdString3() {
		TemplateId id = new TemplateId("system.test");
		assertEquals("system",id.group);
		assertEquals("test",id.name);
		assertTrue(id.variant.isEmpty());
	}

	@Test
	public void testTemplateIdString4() {
		TemplateId id = new TemplateId("system.test.vary");
		assertEquals("system",id.group);
		assertEquals("test",id.name);
		assertEquals("vary",id.variant);
	}

	@Test
	public void testTemplateIdString5() {
		TemplateId id = new TemplateId("system.test.com.ibm.util.vary");
		assertEquals("system",id.group);
		assertEquals("test",id.name);
		assertEquals("com.ibm.util.vary",id.variant);
	}

	@Test
	public void testShorthand1() {
		TemplateId id = new TemplateId("system.test.vary");
		assertEquals("system.test.vary",id.shorthand());
	}

	@Test
	public void testShorthand2() {
		TemplateId id = new TemplateId("system.test");
		assertEquals("system.test.",id.shorthand());
	}

	@Test
	public void testShorthand3() {
		TemplateId id = new TemplateId("system");
		assertEquals("system..",id.shorthand());
	}

}
