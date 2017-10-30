package com.ibm.util.merge.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

public class DataObjectTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDataObject() {
		DataObject object = new DataObject();
		assertEquals(null, object.getParent());
		assertEquals(null, object.getName());
		assertEquals(0, object.getPosition());
	}

	@Test
	public void testIsObject() {
		DataObject object = new DataObject();
		assertTrue(object.isObject());
	}

	@Test
	public void testIsList() {
		DataObject object = new DataObject();
		assertFalse(object.isList());
	}

	@Test
	public void testIsPrimitive() {
		DataObject object = new DataObject();
		assertFalse(object.isPrimitive());
	}

	@Test
	public void testGetAsPrimitive() {
		DataObject object = new DataObject();
		try {
			object.getAsPrimitive();
		} catch (MergeException e) {
			return; // expected
		}
		fail("Get as Primitive failed to throw exception");
	}

	@Test
	public void testGetAsList() {
		DataObject object = new DataObject();
		try {
			object.getAsList();
		} catch (MergeException e) {
			return; // expected
		}
		fail("Get as List failed to throw exception");
	}

	@Test
	public void testGetAsObject() throws Merge500 {
		DataObject object = new DataObject();
		DataElement element = object.getAsObject();
		assertSame(object, element);
	}

	@Test
	public void testMakeArrayFromList() throws Merge500 {
		DataObject object = new DataObject();
		DataList parent = new DataList();
		parent.add(object);
		assertTrue(object.isObject());
		assertEquals(1, parent.size());
		DataElement newElement = object.makeArray();
		assertTrue(newElement.isList());
		assertEquals(1, newElement.getAsList().size());
		assertSame(object, newElement.getAsList().get(0));
		assertSame(newElement, parent.get(0));
	}

	@Test
	public void testMakeArrayFromObject() throws Merge500 {
		DataObject object = new DataObject();
		DataObject parent = new DataObject();
		parent.put("name", object);
		assertTrue(object.isObject());
		assertTrue(parent.containsKey("name"));
		assertTrue(parent.get("name").isObject());
		assertSame(object, parent.get("name"));
		DataElement newElement = object.makeArray();
		assertNotSame(object, parent.get("name"));
		assertTrue(newElement.isList());
		assertEquals(1, newElement.getAsList().size());
		assertSame(object, newElement.getAsList().get(0));
	}

	@Test
	public void testGetSetParent() {
		DataObject object = new DataObject();
		DataObject parent = new DataObject();
		assertEquals(null, object.getParent());
		object.setParent(parent);
		assertSame(parent, object.getParent());
	}
	
	@Test
	public void testGetSetName() {
		DataObject object = new DataObject();
		assertEquals(null, object.getName());
		object.setName("changed");
		assertEquals("changed", object.getName());
	}
	
	@Test
	public void testGetSetPosition() {
		DataObject object = new DataObject();
		assertEquals(0, object.getPosition());
		object.setPosition(4);
		assertEquals(4, object.getPosition());
	}

}
