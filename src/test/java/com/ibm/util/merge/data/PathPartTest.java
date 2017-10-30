package com.ibm.util.merge.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.PathPart;

public class PathPartTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPathPartStringIntBoolean() {
		PathPart part = new PathPart("name",0,false);
		assertEquals("name", part.part);
		assertEquals(0,part.index);
		assertFalse(part.isList);
	}

	@Test
	public void testPathPartString() {
		PathPart part = new PathPart("name");
		assertEquals("name", part.part);
		assertEquals(0,part.index);
		assertFalse(part.isList);
		part = new PathPart("[1]");
		assertEquals("[1]", part.part);
		assertEquals(1,part.index);
		assertTrue(part.isList);
		part = new PathPart("[214]");
		assertEquals("[214]", part.part);
		assertEquals(214,part.index);
		assertTrue(part.isList);
		part = new PathPart("empty");
		assertEquals("empty", part.part);
		assertEquals(0,part.index);
		assertFalse(part.isList);
	}

	@Test
	public void testAsString() {
		PathPart part = new PathPart("name");
		assertEquals("name", part.asString());
		part = new PathPart("[3]");
		assertEquals("[3]", part.asString());
	}
}
