package com.ibm.util.merge.data.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.data.parser.DataProxyXml;
import com.ibm.util.merge.data.parser.sax.XmlSaxHandlerData;
import com.ibm.util.merge.exception.MergeException;

public class DataProxyXmlTest {
	DataManager manager;
	DataProxyXml proxy;

	@Before
	public void setUp() throws Exception {
		manager = new DataManager();
		proxy = new DataProxyXml(new XmlSaxHandlerData());
	}


	@Test
	public void testXmlJsonProxy() {
		assertTrue(proxy instanceof DataProxyXml);
		assertTrue(proxy.saxHandler instanceof XmlSaxHandlerData);
	}

	@Test
	public void testFromXml1() throws IOException, MergeException {
		String xmlString = 
				"<table>" +
				"	<row id=\"r1\" col1=\"r1c1\" col2=\"r1c2\" col3=\"r1c3\" > </row>" +
				"	<row id=\"r2\" col1=\"r2c1\" col2=\"r2c2\" col3=\"r2c3\" > </row>" +
				"	<row id=\"r3\" col1=\"r3c1\" col2=\"r3c2\" col3=\"r3c3\" > </row>" +
				"</table>";
		
		DataElement element = proxy.fromXML(xmlString);
		manager.put("test", "-", element);
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.contians("test-table", "-"));
		assertTrue(manager.contians("test-table-row", "-"));
		assertTrue(manager.get("test-table-row", "-").isList());
		assertEquals(3, manager.get("test-table-row", "-").getAsList().size());
        assertEquals("r1",   manager.get("test-table-row-[0]-id","-").getAsPrimitive());
        assertEquals("r1c1", manager.get("test-table-row-[0]-col1","-").getAsPrimitive());
        assertEquals("r1c2", manager.get("test-table-row-[0]-col2","-").getAsPrimitive());
        assertEquals("r1c3", manager.get("test-table-row-[0]-col3","-").getAsPrimitive());
        assertEquals("r2",   manager.get("test-table-row-[1]-id","-").getAsPrimitive());
        assertEquals("r2c1", manager.get("test-table-row-[1]-col1","-").getAsPrimitive());
        assertEquals("r2c2", manager.get("test-table-row-[1]-col2","-").getAsPrimitive());
        assertEquals("r2c3", manager.get("test-table-row-[1]-col3","-").getAsPrimitive());
        assertEquals("r3",   manager.get("test-table-row-[2]-id","-").getAsPrimitive());
        assertEquals("r3c1", manager.get("test-table-row-[2]-col1","-").getAsPrimitive());
        assertEquals("r3c2", manager.get("test-table-row-[2]-col2","-").getAsPrimitive());
        assertEquals("r3c3", manager.get("test-table-row-[2]-col3","-").getAsPrimitive());
	}

	@Test
	public void testFromXml2() throws IOException, MergeException {
		DataManager manager = new DataManager();
		DataProxyXml proxy;
		proxy = new DataProxyXml(new XmlSaxHandlerData());

		String xmlString = 
				"<table>" +
				"	<row><id>r1</id><col1>r1c1</col1><col2>r1c2</col2><col3>r1c3</col3></row>" +
				"	<row><id>r2</id><col1>r2c1</col1><col2>r2c2</col2><col3>r2c3</col3></row>" +
				"	<row><id>r3</id><col1>r3c1</col1><col2>r3c2</col2><col3>r3c3</col3></row>" +
				"</table>";
		
		DataElement element = proxy.fromXML(xmlString);
		manager.put("test", "-", element);
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.contians("test-table", "-"));
		assertTrue(manager.contians("test-table-row", "-"));
		assertTrue(manager.get("test-table-row", "-").isList());
		assertEquals(3, manager.get("test-table-row", "-").getAsList().size());
        assertEquals("r1",   manager.get("test-table-row-[0]-id","-").getAsPrimitive());
        assertEquals("r1c1", manager.get("test-table-row-[0]-col1","-").getAsPrimitive());
        assertEquals("r1c2", manager.get("test-table-row-[0]-col2","-").getAsPrimitive());
        assertEquals("r1c3", manager.get("test-table-row-[0]-col3","-").getAsPrimitive());
        assertEquals("r2",   manager.get("test-table-row-[1]-id","-").getAsPrimitive());
        assertEquals("r2c1", manager.get("test-table-row-[1]-col1","-").getAsPrimitive());
        assertEquals("r2c2", manager.get("test-table-row-[1]-col2","-").getAsPrimitive());
        assertEquals("r2c3", manager.get("test-table-row-[1]-col3","-").getAsPrimitive());
        assertEquals("r3",   manager.get("test-table-row-[2]-id","-").getAsPrimitive());
        assertEquals("r3c1", manager.get("test-table-row-[2]-col1","-").getAsPrimitive());
        assertEquals("r3c2", manager.get("test-table-row-[2]-col2","-").getAsPrimitive());
        assertEquals("r3c3", manager.get("test-table-row-[2]-col3","-").getAsPrimitive());
	}
	
	@Test
	public void testFromXml3() throws IOException, MergeException {
		DataManager manager = new DataManager();
		DataProxyXml proxy;
		proxy = new DataProxyXml(new XmlSaxHandlerData());

		String xmlString = 
				"<table>" +
				"	<row id=\"r1\" col1=\"r1c1\"><col2>r1c2</col2><col3>r1c3</col3></row>" +
				"	<row id=\"r2\" col1=\"r2c1\"><col2>r2c2</col2><col3>r2c3</col3></row>" +
				"	<row id=\"r3\" col1=\"r3c1\"><col2>r3c2</col2><col3>r3c3</col3></row>" +
				"</table>";
		
		DataElement element = proxy.fromXML(xmlString);
		manager.put("test", "-", element);
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.contians("test-table", "-"));
		assertTrue(manager.contians("test-table-row", "-"));
		assertTrue(manager.get("test-table-row", "-").isList());
		assertEquals(3, manager.get("test-table-row", "-").getAsList().size());
        assertEquals("r1",   manager.get("test-table-row-[0]-id","-").getAsPrimitive());
        assertEquals("r1c1", manager.get("test-table-row-[0]-col1","-").getAsPrimitive());
        assertEquals("r1c2", manager.get("test-table-row-[0]-col2","-").getAsPrimitive());
        assertEquals("r1c3", manager.get("test-table-row-[0]-col3","-").getAsPrimitive());
        assertEquals("r2",   manager.get("test-table-row-[1]-id","-").getAsPrimitive());
        assertEquals("r2c1", manager.get("test-table-row-[1]-col1","-").getAsPrimitive());
        assertEquals("r2c2", manager.get("test-table-row-[1]-col2","-").getAsPrimitive());
        assertEquals("r2c3", manager.get("test-table-row-[1]-col3","-").getAsPrimitive());
        assertEquals("r3",   manager.get("test-table-row-[2]-id","-").getAsPrimitive());
        assertEquals("r3c1", manager.get("test-table-row-[2]-col1","-").getAsPrimitive());
        assertEquals("r3c2", manager.get("test-table-row-[2]-col2","-").getAsPrimitive());
        assertEquals("r3c3", manager.get("test-table-row-[2]-col3","-").getAsPrimitive());
	}

	@Test
	public void testFromXml4() throws IOException, MergeException {
		DataManager manager = new DataManager();
		DataProxyXml proxy;
		proxy = new DataProxyXml(new XmlSaxHandlerData());

		String xmlString = 
				"<table>" +
				"	<row id=\"r1\" col1=\"r1c1\"><col2>r1c2</col2><col3>r1c3</col3></row>" +
						"Foo" +
				"	<row id=\"r2\" col1=\"r2c1\"><col2>r2c2</col2><col3>r2c3</col3></row>" +
						"Foo" +
				"	<row id=\"r3\" col1=\"r3c1\"><col2>r3c2</col2><col3>r3c3</col3></row>" +
				"</table>";
		
		DataElement element = proxy.fromXML(xmlString);
		manager.put("test", "-", element);
		assertTrue(manager.contians("test", "-"));
		assertTrue(manager.contians("test-table", "-"));
		assertTrue(manager.contians("test-table-row", "-"));
		assertTrue(manager.get("test-table-row", "-").isList());
		assertEquals(5, manager.get("test-table-row", "-").getAsList().size());
        assertEquals("r1",   manager.get("test-table-row-[0]-id","-").getAsPrimitive());
        assertEquals("r1c1", manager.get("test-table-row-[0]-col1","-").getAsPrimitive());
        assertEquals("r1c2", manager.get("test-table-row-[0]-col2","-").getAsPrimitive());
        assertEquals("r1c3", manager.get("test-table-row-[0]-col3","-").getAsPrimitive());
        assertEquals("Foo",  manager.get("test-table-row-[1]", "-").getAsPrimitive());
        assertEquals("r2",   manager.get("test-table-row-[2]-id","-").getAsPrimitive());
        assertEquals("r2c1", manager.get("test-table-row-[2]-col1","-").getAsPrimitive());
        assertEquals("r2c2", manager.get("test-table-row-[2]-col2","-").getAsPrimitive());
        assertEquals("r2c3", manager.get("test-table-row-[2]-col3","-").getAsPrimitive());
        assertEquals("Foo",  manager.get("test-table-row-[3]", "-").getAsPrimitive());
        assertEquals("r3",   manager.get("test-table-row-[4]-id","-").getAsPrimitive());
        assertEquals("r3c1", manager.get("test-table-row-[4]-col1","-").getAsPrimitive());
        assertEquals("r3c2", manager.get("test-table-row-[4]-col2","-").getAsPrimitive());
        assertEquals("r3c3", manager.get("test-table-row-[4]-col3","-").getAsPrimitive());
	}

}
