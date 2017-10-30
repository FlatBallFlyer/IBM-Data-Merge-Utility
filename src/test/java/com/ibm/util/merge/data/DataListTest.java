package com.ibm.util.merge.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

public class DataListTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDataList() {
		DataList list = new DataList();
		assertEquals(null, list.getParent());
		assertEquals(null, list.getName());
		assertEquals(0, list.getPosition());
		assertEquals(0, list.size());
	}

	@Test
	public void testAddPrimitive() {
		DataList list = new DataList();
		DataPrimitive p1 = new DataPrimitive("foo");
		list.add(p1);
		assertSame(p1.getParent(), list);
		assertEquals(0, p1.getPosition());
		assertEquals(null, p1.getName());
	}

	@Test
	public void testAddList() {
		DataList list = new DataList();
		DataList p2 = new DataList();
		list.add(p2);
		assertSame(p2.getParent(), list);
		assertEquals(0, p2.getPosition());
		assertEquals(null, p2.getName());
	}

	@Test
	public void testAddObject() throws Merge500 {
		DataList list = new DataList();
		DataObject p3 = new DataObject();
		list.add(p3);
		assertSame(p3.getParent(), list);
		assertEquals(0, p3.getPosition());
		assertEquals(null, p3.getName());
	}

	@Test
	public void testSetFail() {
		DataList list = new DataList();
		DataPrimitive p1 = new DataPrimitive("foo");
		try {
			list.set(1, p1);
		} catch (IndexOutOfBoundsException e) {
			return; // expected 
		}
		fail("set out-of-bounds failed did not throw error");
	}
	
	@Test
	public void testSetPrimitive() throws Merge500 {
		DataList list = new DataList();
		DataPrimitive p1 = new DataPrimitive("foo");
		DataPrimitive p2 = new DataPrimitive("bar");
		list.add(p1);
		assertEquals("foo", list.get(0).getAsPrimitive());
		list.set(0, p2);
		assertEquals("bar", list.get(0).getAsPrimitive());
	}
	
	@Test
	public void testSetList() throws Merge500 {
		DataList list = new DataList();
		DataPrimitive p1 = new DataPrimitive("foo");
		DataList l1 = new DataList();
		list.add(p1);
		assertEquals("foo", list.get(0).getAsPrimitive());
		list.set(0, l1);
		assertTrue(list.get(0).isList());
		assertSame(list.get(0), l1);
	}
	
	@Test
	public void testSetObject() throws Merge500 {
		DataList list = new DataList();
		DataPrimitive p1 = new DataPrimitive("foo");
		DataObject o1 = new DataObject();
		list.add(p1);
		assertEquals("foo", list.get(0).getAsPrimitive());
		list.set(0, o1);
		assertTrue(list.get(0).isObject());
		assertSame(list.get(0), o1);
	}
	
	@Test
	public void testIsObject() {
		DataList list = new DataList();
		assertFalse(list.isObject());
	}

	@Test
	public void testIsList() {
		DataList list = new DataList();
		assertTrue(list.isList());
	}

	@Test
	public void testIsPrimitive() {
		DataList list = new DataList();
		assertFalse(list.isPrimitive());
	}

	@Test
	public void testGetAsPrimitive() {
		DataList list = new DataList();
		try {
			list.getAsPrimitive();
		} catch (MergeException e) {
			return; //expected
		}
		fail("Get As Primitive failed to throw exception");
	}

	@Test
	public void testGetAsList() {
		DataList list = new DataList();
		DataElement aList = list.getAsList();
		assertSame(list, aList);
	}

	@Test
	public void testGetAsObject() {
		DataList list = new DataList();
		try {
			list.getAsObject();
		} catch (MergeException e) {
			return; //expected
		}
		fail("Get As Object failed to throw exception");
	}

	@Test
	public void testMakeArrayFromList() throws Merge500 {
		DataList list = new DataList();
		DataList parent = new DataList();
		parent.add(list);
		assertTrue(list.isList());
		assertEquals(1, parent.size());
		DataElement newElement = list.makeArray();
		assertTrue(newElement.isList());
		assertEquals(0, newElement.getAsList().size());
		assertSame(list, newElement);
		assertSame(newElement, parent.get(0));
	}

	@Test
	public void testMakeArrayFromObject() throws Merge500 {
		DataList list = new DataList();
		DataObject object = new DataObject();
		object.put("name", list);
		assertTrue(list.isList());
		assertTrue(object.containsKey("name"));
		DataElement newElement = list.makeArray();
		assertTrue(newElement.isList());
		assertEquals(0, newElement.getAsList().size());
		assertSame(list, newElement);
	}

	@Test
	public void testGetSetParent() {
		DataList list = new DataList();
		assertEquals(null, list.getParent());
		DataPrimitive parent = new DataPrimitive("Bar");
		list.setParent(parent);
		assertSame(parent, list.getParent());
	}
	
	@Test
	public void testGetSetName() {
		DataList list = new DataList();
		assertEquals(null, list.getName());
		list.setName("changed");
		assertEquals("changed", list.getName());
	}
	
	@Test
	public void testGetSetPosition() {
		DataList list = new DataList();
		assertEquals(0, list.getPosition());
		list.setPosition(4);
		assertEquals(4, list.getPosition());
	}
}
