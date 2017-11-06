package com.ibm.util.merge.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

public class DataPrimitiveTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDataPrimitiveString() {
		DataPrimitive primitive = new DataPrimitive("Foo");
		assertEquals(null, primitive.getParent());
		assertEquals(null, primitive.getName());
		assertEquals("Foo", primitive.get());
	}

	@Test
	public void testDataPrimitiveInt() {
		DataPrimitive primitive = new DataPrimitive(7);
		assertEquals(null, primitive.getParent());
		assertEquals(null, primitive.getName());
		assertEquals("7", primitive.get());
	}

	@Test
	public void testIsObject() {
		DataPrimitive primitive = new DataPrimitive("");
		assertFalse(primitive.isObject());
	}

	@Test
	public void testIsList() {
		DataPrimitive primitive = new DataPrimitive("");
		assertFalse(primitive.isList());
	}

	@Test
	public void testIsPrimitive() {
		DataPrimitive primitive = new DataPrimitive("");
		assertTrue(primitive.isPrimitive());
	}

	@Test
	public void testGetAsPrimitive() throws Merge500 {
		DataPrimitive primitive = new DataPrimitive("Foo");
		assertEquals("Foo", primitive.getAsPrimitive());
	}

	@Test
	public void testGetAsList() {
		DataPrimitive primitive = new DataPrimitive("Foo");
		try {
			primitive.getAsList();
		} catch (MergeException e) {
			return; // expected
		}
		fail("Get as List failed to throw exception");
	}

	@Test
	public void testGetAsObject() {
		DataPrimitive primitive = new DataPrimitive("Foo");
		try {
			primitive.getAsObject();
		} catch (MergeException e) {
			return; // expected
		}
		fail("Get as Object failed to throw exception");
	}

	@Test
	public void testMakeArrayFromList() throws Merge500 {
		DataElement primitive = new DataPrimitive("Foo");
		DataList list = new DataList();
		list.add(primitive);
		assertTrue(primitive.isPrimitive());
		assertEquals("Foo", primitive.getAsPrimitive());
		DataElement newElement = primitive.makeArray();
		assertTrue(newElement.isList());
		assertEquals(1, newElement.getAsList().size());
		assertSame(primitive, newElement.getAsList().get(0));
	}

	@Test
	public void testMakeArrayFromObject() throws Merge500 {
		DataElement primitive = new DataPrimitive("Foo");
		DataObject object = new DataObject();
		object.put("name", primitive);
		assertTrue(primitive.isPrimitive());
		assertEquals("Foo", primitive.getAsPrimitive());
		DataElement newElement = primitive.makeArray();
		assertTrue(newElement.isList());
		assertEquals(1, newElement.getAsList().size());
		assertSame(primitive, newElement.getAsList().get(0));
	}

	@Test
	public void testGetSetParent() {
		DataPrimitive primitive = new DataPrimitive("Foo");
		assertEquals(null, primitive.getParent());
		DataPrimitive parent = new DataPrimitive("Bar");
		primitive.setParent(parent);
		assertSame(parent, primitive.getParent());
	}
	
	@Test
	public void testGetSetName() {
		DataPrimitive primitive = new DataPrimitive("Foo");
		assertEquals(null, primitive.getName());
		primitive.setName("changed");
		assertEquals("changed", primitive.getName());
	}
	
	@Test
	public void testGetSetPosition() {
		DataPrimitive primitive = new DataPrimitive("Foo");
		assertEquals(0, primitive.getPosition());
		primitive.setPosition(4);
		assertEquals(4, primitive.getPosition());
	}
}
