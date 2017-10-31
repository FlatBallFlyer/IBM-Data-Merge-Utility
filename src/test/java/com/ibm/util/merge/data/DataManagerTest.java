package com.ibm.util.merge.data;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.exception.MergeException;

public class DataManagerTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDataManager() throws MergeException {
		DataManager manager = new DataManager();
		assertEquals(0, manager.size());
		assertEquals(0, manager.contextStackSize());
	}

	@Test
	public void testSizeClear() throws MergeException {
		DataManager manager = new DataManager();
		assertEquals(0, manager.size());
		assertEquals(0, manager.contextStackSize());
		manager.put("test", "-", "TestValue");
		assertEquals(1, manager.size());
		manager.clear();
		assertEquals(0, manager.size());
	}

	@Test
	public void testPushGetPopContext() throws MergeException {
		DataManager manager = new DataManager();
		DataElement one = new DataList();
		DataElement two = new DataObject();
		assertEquals(0, manager.size());
		assertEquals(0, manager.contextStackSize());
		manager.pushContext(one);
		assertEquals(1, manager.contextStackSize());
		assertTrue(manager.get(Merger.IDMU_CONTEXT, "-").isList());
		manager.pushContext(two);
		assertEquals(2, manager.contextStackSize());
		assertTrue(manager.get(Merger.IDMU_CONTEXT, "-").isObject());
		manager.popContext();
		assertEquals(1, manager.contextStackSize());
		assertTrue(manager.get(Merger.IDMU_CONTEXT, "-").isList());
	}

	@Test
	public void testContainsContext() throws MergeException {
		DataManager manager = new DataManager();
		DataElement one = new DataList();
		assertEquals(0, manager.size());
		assertEquals(0, manager.contextStackSize());
		assertFalse(manager.contians(Merger.IDMU_CONTEXT, "-"));
		manager.pushContext(one);
		assertEquals(1, manager.contextStackSize());
		assertTrue(manager.contians(Merger.IDMU_CONTEXT, "-"));
	}

	@Test
	public void testContains() throws MergeException {
		DataManager manager = new DataManager();
		DataObject obj = new DataObject();
		DataObject id = new DataObject();
		id.put("group", new DataPrimitive("foo"));
		id.put("name", new DataPrimitive("bar"));
		id.put("variant", new DataPrimitive("bam"));
		obj.put("id", id);
		DataList list = new DataList();
		obj.put("directives", list);
		list.add(new DataObject());
		DataObject obj2 = new DataObject();
		obj2.put("two", new DataPrimitive("boo"));
		list.add(obj2);
		list.add(new DataObject());
		
		manager.put("test", "-", obj);
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.contians("test-id", "-"));
		assertTrue(manager.contians("test-id-group", "-"));
		assertTrue(manager.contians("test-id-name", "-"));
		assertTrue(manager.contians("test-id-variant", "-"));
		assertTrue(manager.contians("test-directives", "-"));
		assertTrue(manager.contians("test-directives-[0]", "-"));
		assertTrue(manager.contians("test-directives-[1]", "-"));
		assertTrue(manager.contians("test-directives-[1]-two", "-"));
		assertTrue(manager.contians("test/directives/[1]/two", "/"));
		assertTrue(manager.contians("test-directives-[2]", "-"));
		assertFalse(manager.contians("test-group", "-"));
		assertFalse(manager.contians("test-name", "-"));
		assertFalse(manager.contians("test-directives-[3]", "-"));
	}
	
	@Test
	public void testPutParameterMap() throws MergeException {
		DataManager manager = new DataManager();
		HashMap<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("Parm1", new String[] {"Value1"} );
		parameters.put("Parm2", new String[] {"Value2a","Value2b","Value2c"} );
		parameters.put("Parm3", new String[] {} );
		manager.put("test", "-", parameters);
		assertTrue(manager.contians("test-Parm1", "-"));
		assertTrue(manager.get("test-Parm1", "-").isList());
		assertEquals("Value1", manager.get("test-Parm1-[0]", "-").getAsPrimitive());
		assertTrue(manager.contians("test-Parm2", "-"));
		assertTrue(manager.contians("test-Parm2-[0]", "-"));
		assertEquals("Value2a", manager.get("test-Parm2-[0]", "-").getAsPrimitive());
		assertTrue(manager.contians("test-Parm2-[1]", "-"));
		assertEquals("Value2b", manager.get("test-Parm2-[1]", "-").getAsPrimitive());
		assertTrue(manager.contians("test-Parm2-[2]", "-"));
		assertEquals("Value2c", manager.get("test-Parm2-[2]", "-").getAsPrimitive());
		assertTrue(manager.contians("test-Parm3", "-"));
		assertTrue(manager.get("test-Parm3",  "-").isList());
		assertEquals(0, manager.get("test-Parm3",  "-").getAsList().size());
	}

	@Test
	public void testPutElementObject() throws MergeException {
		DataManager manager = new DataManager();
		DataObject newObject1 = new DataObject();
		manager.put("test", "-", newObject1);
		manager.put("test-one", "-", "Value1");
		manager.put("test-two", "-", "Value2");
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.contians("test-one", "-"));
		assertTrue(manager.contians("test-two", "-"));
		assertEquals("Value1", manager.get("test-one", "-").getAsPrimitive());
		assertEquals("Value2", manager.get("test-two", "-").getAsPrimitive());
		DataObject newObject2 = new DataObject();
		newObject2.put("one", new DataPrimitive("ValueA"));
		newObject2.put("two", new DataPrimitive("ValueB"));
		manager.put("test", "-", newObject2);
		assertTrue(manager.get("test", "-").isList());
		assertEquals(2, manager.get("test", "-").getAsList().size());
		assertEquals("Value1", manager.get("test-[0]-one", "-").getAsPrimitive());
		assertEquals("Value2", manager.get("test-[0]-two", "-").getAsPrimitive());
		assertEquals("ValueA", manager.get("test-[1]-one", "-").getAsPrimitive());
		assertEquals("ValueB", manager.get("test-[1]-two", "-").getAsPrimitive());
	}
	
	@Test
	public void testPutElementList() throws MergeException {
		DataManager manager = new DataManager();
		DataList newArray = new DataList();
		manager.put("test", "-", newArray);
		manager.put("test", "-", "Value1");
		manager.put("test", "-", "Value2");
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.get("test", "-").isList());
		assertEquals(2,manager.get("test", "-").getAsList().size());
		assertTrue(manager.contians("test-[0]", "-"));
		assertTrue(manager.contians("test-[1]", "-"));
		assertEquals("Value1", manager.get("test-[0]", "-").getAsPrimitive());
		assertEquals("Value2", manager.get("test-[1]", "-").getAsPrimitive());
	}
	
	@Test
	public void testPutListOfList() throws MergeException {
		DataManager manager = new DataManager();
		DataList newArray = new DataList();
		manager.put("test", "-", newArray);
		manager.put("test", "-", new DataList());
		manager.put("test", "-", new DataList());
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.get("test", "-").isList());
		assertEquals(2, manager.get("test", "-").getAsList().size());
		assertTrue(manager.contians("test-[0]", "-"));
		assertTrue(manager.contians("test-[1]", "-"));
		assertTrue(manager.get("test-[0]", "-").isList());
		assertEquals(0,manager.get("test-[0]", "-").getAsList().size());
		assertTrue(manager.get("test-[1]", "-").isList());
		assertEquals(0,manager.get("test-[1]", "-").getAsList().size());
		manager.put("test-[1]", "-", "TestValue1-1");
		manager.put("test-[1]", "-", "TestValue1-2");
		assertEquals("TestValue1-1", manager.get("test-[1]-[0]", "-").getAsPrimitive());
		assertEquals("TestValue1-2", manager.get("test-[1]-[1]", "-").getAsPrimitive());
	}
	
	@Test
	public void testPutString() throws MergeException {
		DataManager manager = new DataManager();
		manager.put("test", "-", "TestValue");
		assertTrue(manager.contians("test", "-"));
		assertEquals("TestValue", manager.get("test", "-").getAsPrimitive());
		manager.put("test", "-", "TestValue2");
		manager.put("test", "-", "TestValue3");
		manager.put("test", "-", "TestValue3");
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.get("test", "-").isList());
		assertEquals(4, manager.get("test",  "-").getAsList().size());
		assertEquals("TestValue", manager.get("test-[0]", "-").getAsPrimitive());
		assertEquals("TestValue2", manager.get("test-[1]", "-").getAsPrimitive());
		assertEquals("TestValue3", manager.get("test-[2]", "-").getAsPrimitive());
	}

	@Test
	public void testPutStringOver() throws MergeException {
		DataManager manager = new DataManager();
		manager.put("test", "-", new DataObject());
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.get("test", "-").isObject());
		assertTrue(manager.get("test", "-").getAsObject().entrySet().isEmpty());
		
		manager.put("test", "-", new DataPrimitive("Foo"));
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.get("test", "-").isList());
		assertTrue(manager.get("test-[0]", "-").isObject());
		assertTrue(manager.get("test-[1]", "-").isPrimitive());
		assertEquals("Foo", manager.get("test-[1]", "-").getAsPrimitive());

		manager.put("test", "-", new DataPrimitive("Bar"));
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.get("test", "-").isList());
		assertTrue(manager.get("test-[0]", "-").isObject());
		assertTrue(manager.get("test-[1]", "-").isPrimitive());
		assertTrue(manager.get("test-[2]", "-").isPrimitive());
		assertEquals("Foo", manager.get("test-[1]", "-").getAsPrimitive());
		assertEquals("Bar", manager.get("test-[2]", "-").getAsPrimitive());
	}

}