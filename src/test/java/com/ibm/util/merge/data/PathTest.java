package com.ibm.util.merge.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.Path;
import com.ibm.util.merge.data.PathPart;
import com.ibm.util.merge.exception.MergeException;

public class PathTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPathStringString1() {
		Path test = new Path("Foo", "-");
		assertEquals(1, test.size());
		assertEquals("-", test.getSeparator());
		assertEquals("Foo", test.get(0).part);
	}
	
	@Test
	public void testPathStringString2() {
		Path test = new Path("Foo-bar", "-");
		assertEquals(2, test.size());
		assertEquals("-", test.getSeparator());
		assertEquals("Foo", test.get(0).part);
		assertEquals("bar", test.get(1).part);
	}
	
	@Test
	public void testPathStringString3() {
		Path test = new Path("Foo-[2]-bar", "-");
		assertEquals(3, test.size());
		assertEquals("-", test.getSeparator());
		assertEquals("Foo", test.get(0).asString());
		assertEquals("[2]", test.get(1).asString());
		assertEquals("bar", test.get(2).asString());
	}
	
	@Test
	public void testPathPath() {
		Path test = new Path("Foo-[2]-bar", "-");
		Path dup = new Path(test);
		assertNotSame(test,dup);
		assertEquals(test.getPath(), dup.getPath());
	}
	
	@Test
	public void testGetPathParts1() throws MergeException {
		Path test = new Path("Foo", "\"");
		assertEquals(1, test.size());
		assertEquals("Foo", test.get(0).part);
		assertEquals(0, test.get(0).index);
		assertFalse(test.get(0).isList);
	}

	@Test
	public void testGetPathParts2() throws MergeException {
		Path test = new Path("Foo\"bar", "\"");
		assertEquals("Foo\"bar", test.getPath());
		assertEquals(2, test.size());
		assertEquals("Foo", test.get(0).part);
		assertEquals(0, test.get(0).index);
		assertFalse(test.get(0).isList);
		assertEquals("bar", test.get(1).part);
		assertEquals(0, test.get(1).index);
		assertFalse(test.get(1).isList);
	}

	@Test
	public void testGetPathParts3() throws MergeException {
		Path test = new Path("Foo\"bar\"[3]", "\"");
		assertEquals("Foo\"bar\"[3]", test.getPath());
		assertEquals(3, test.size());
		assertEquals("Foo", test.get(0).part);
		assertEquals(0, test.get(0).index);
		assertFalse(test.get(0).isList);
		assertEquals("bar", test.get(1).part);
		assertFalse(test.get(1).isList);
		assertEquals(3, test.get(2).index);
		assertTrue(test.get(2).isList);
	}

	@Test
	public void testGetPathParts4() throws MergeException {
		Path test = new Path("Foo\"bar\"[3]\"fred\"betty", "\"");
		assertEquals("Foo\"bar\"[3]\"fred\"betty", test.getPath());
		assertEquals(5, test.size());
		assertEquals("Foo", test.get(0).part);
		assertEquals(0, test.get(0).index);
		assertFalse(test.get(0).isList);
		assertEquals("bar", test.get(1).part);
		assertFalse(test.get(1).isList);
		assertEquals(3, test.get(2).index);
		assertTrue(test.get(2).isList);
		assertEquals("fred", test.get(3).part);
		assertEquals(0, test.get(3).index);
		assertFalse(test.get(3).isList);
		assertEquals("betty", test.get(4).part);
		assertEquals(0, test.get(4).index);
		assertFalse(test.get(4).isList);
	}
	
	@Test
	public void testGetCurrent() throws MergeException {
		Path test = new Path("Foo\"bar\"[3]\"fred\"betty", "\"");
		assertEquals("Foo\"bar\"[3]\"fred\"betty", test.getPath());
		assertEquals(5, test.size());
		assertEquals("", test.getCurrent());
		assertEquals("Foo", test.pop().part);
		assertEquals("Foo", test.getCurrent());
		assertEquals("bar", test.pop().part);
		assertEquals("Foo\"bar", test.getCurrent());
		assertEquals("[3]", test.pop().part);
		assertEquals("Foo\"bar\"[3]", test.getCurrent());
		assertEquals("fred", test.pop().part);
		assertEquals("Foo\"bar\"[3]\"fred", test.getCurrent());
		assertEquals("betty", test.pop().part);
		assertEquals("Foo\"bar\"[3]\"fred\"betty", test.getCurrent());
	}
	
	@Test
	public void testGetPathPartsPeriod() throws MergeException {
		Path test = new Path("Foo.fred.[3].fred", ".");
		assertEquals("", test.getPath());
		assertEquals(0, test.size());
	}

	@Test
	public void testGetPathPartsWrong() throws MergeException {
		Path test = new Path("Foo-fred[3]-fred", "-");
		assertEquals("Foo-fred[3]-fred", test.getPath());
		assertEquals(3, test.size());
		assertEquals("fred[3]", test.get(1).part);
		assertEquals(0, test.get(1).index);
		assertFalse(test.get(1).isList);
	}

	@Test
	public void testPopSize() {
		Path test = new Path("Foo\"bar\"[3]\"fred\"betty", "\"");
		PathPart part;
		assertEquals(5, test.size());
		part = test.pop();
		assertEquals(4, test.size());
		assertEquals("Foo", part.part);
		assertEquals(0, part.index);
		assertFalse(part.isList);
		part = test.pop();
		assertEquals(3, test.size());
		assertEquals("bar", part.part);
		assertEquals(0, part.index);
		assertFalse(part.isList);
		part = test.pop();
		assertEquals(2, test.size());
		assertEquals("[3]", part.part);
		assertEquals(3, part.index);
		assertTrue(part.isList);
		part = test.pop();
		assertEquals(1, test.size());
		assertEquals("fred", part.part);
		assertEquals(0, part.index);
		assertFalse(part.isList);
		part = test.pop();
		assertEquals(0, test.size());
		assertEquals("betty", part.part);
		assertEquals(0, part.index);
		assertFalse(part.isList);
	}

	@Test
	public void testAddRemove() {
		Path test = new Path("Foo", "-");
		assertEquals(1, test.size());
		assertEquals("Foo", test.get(0).asString());
		assertEquals("Foo", test.getPath());
		test.add("bar");
		assertEquals(2, test.size());
		assertEquals("Foo-bar", test.getPath());
		test.add("[3]");
		assertEquals(3, test.size());
		assertEquals("Foo-bar-[3]", test.getPath());
		PathPart off = test.remove();
		assertEquals(2, test.size());
		assertEquals("[3]", off.asString());
		assertEquals("Foo-bar", test.getPath());
		off = test.remove();
		assertEquals(1, test.size());
		assertEquals("bar", off.asString());
		assertEquals("Foo", test.getPath());
		off = test.remove();
		assertEquals(0, test.size());
	}
	
	@Test
	public void testPush() {
		Path test = new Path("value","-");
		assertEquals(1, test.size());
		test.push(new PathPart("", 3, true));
		assertEquals(2, test.size());
		test.push(new PathPart("bar", 0, false));
		assertEquals(3, test.size());
		test.push(new PathPart("foo", 0, false));
		assertEquals(4, test.size());
		assertEquals("foo-bar-[3]-value", test.getPath());
	}
}
