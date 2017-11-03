package com.ibm.util.merge.data.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.data.parser.DataProxyXmlStrict;
import com.ibm.util.merge.exception.MergeException;

public class DataProxyXmlStrictTest {
	DataProxyJson gsonProxy = new DataProxyJson();
	DataManager manager;
	DataProxyXmlStrict proxy;

	@Before
	public void setUp() throws Exception {
		manager = new DataManager();
		proxy = new DataProxyXmlStrict();
	}


	@Test
	public void testXmlJsonProxy() {
		assertTrue(proxy instanceof DataProxyXmlStrict);
	}

	@Test
	public void testFromXml1() throws IOException, MergeException {
		String xmlString = 
				"<?xml version = \"1.0\"?>" +
				"<table>" +
				"	<row id=\"r1\" col1=\"r1c1\" col2=\"r1c2\" col3=\"r1c3\" > </row>" +
				"	<row id=\"r2\" col1=\"r2c1\" col2=\"r2c2\" col3=\"r2c3\" > </row>" +
				"	<row id=\"r3\" col1=\"r3c1\" col2=\"r3c2\" col3=\"r3c3\" > </row>" +
				"</table>";
		
		DataElement element = proxy.fromXML(xmlString);
		manager.put("test", "-", element);
		assertTrue(manager.contians("test", "-"));
		assertEquals("table",manager.get("test-name", "-").getAsPrimitive());
		assertEquals("row",  manager.get("test-members-[0]-name", "-").getAsPrimitive());
        assertEquals("r1",   manager.get("test-members-[0]-attrs-id","-").getAsPrimitive());
        assertEquals("r1c1", manager.get("test-members-[0]-attrs-col1","-").getAsPrimitive());
        assertEquals("r1c2", manager.get("test-members-[0]-attrs-col2","-").getAsPrimitive());
        assertEquals("r1c3", manager.get("test-members-[0]-attrs-col3","-").getAsPrimitive());
        assertEquals("r2",   manager.get("test-members-[1]-attrs-id","-").getAsPrimitive());
        assertEquals("r2c1", manager.get("test-members-[1]-attrs-col1","-").getAsPrimitive());
        assertEquals("r2c2", manager.get("test-members-[1]-attrs-col2","-").getAsPrimitive());
        assertEquals("r2c3", manager.get("test-members-[1]-attrs-col3","-").getAsPrimitive());
        assertEquals("r3",   manager.get("test-members-[2]-attrs-id","-").getAsPrimitive());
        assertEquals("r3c1", manager.get("test-members-[2]-attrs-col1","-").getAsPrimitive());
        assertEquals("r3c2", manager.get("test-members-[2]-attrs-col2","-").getAsPrimitive());
        assertEquals("r3c3", manager.get("test-members-[2]-attrs-col3","-").getAsPrimitive());
	}

	@Test
	public void testFromXml2() throws IOException, MergeException {
		String xmlString = 
				"<table>" +
				"	<row><id>r1</id><col1>r1c1</col1><col2>r1c2</col2><col3>r1c3</col3></row>" +
				"	<row><id>r2</id><col1>r2c1</col1><col2>r2c2</col2><col3>r2c3</col3></row>" +
				"	<row><id>r3</id><col1>r3c1</col1><col2>r3c2</col2><col3>r3c3</col3></row>" +
				"</table>";
		
		DataElement element = proxy.fromXML(xmlString);
		manager.put("test", "-", element);
		assertEquals("table",manager.get("test-name", "-").getAsPrimitive());
		assertEquals("row",  manager.get("test-members-[0]-name", "-").getAsPrimitive());
        assertEquals("id",   manager.get("test-members-[0]-members-[0]-name","-").getAsPrimitive());
        assertEquals("r1",   manager.get("test-members-[0]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r1c1", manager.get("test-members-[0]-members-[1]-members-[0]","-").getAsPrimitive());
        assertEquals("r1c2", manager.get("test-members-[0]-members-[2]-members-[0]","-").getAsPrimitive());
        assertEquals("r1c3", manager.get("test-members-[0]-members-[3]-members-[0]","-").getAsPrimitive());
        assertEquals("r2",   manager.get("test-members-[1]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r2c1", manager.get("test-members-[1]-members-[1]-members-[0]","-").getAsPrimitive());
        assertEquals("r2c2", manager.get("test-members-[1]-members-[2]-members-[0]","-").getAsPrimitive());
        assertEquals("r2c3", manager.get("test-members-[1]-members-[3]-members-[0]","-").getAsPrimitive());
        assertEquals("r3",   manager.get("test-members-[2]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r3c1", manager.get("test-members-[2]-members-[1]-members-[0]","-").getAsPrimitive());
        assertEquals("r3c2", manager.get("test-members-[2]-members-[2]-members-[0]","-").getAsPrimitive());
        assertEquals("r3c3", manager.get("test-members-[2]-members-[3]-members-[0]","-").getAsPrimitive());
	}
	
	@Test
	public void testFromXml3() throws IOException, MergeException {
		String xmlString = 
				"<table>" +
				"	<row id=\"r1\" col1=\"r1c1\"><col2>r1c2</col2><col3>r1c3</col3>" +
				"		<sub type=\"foo\" value=\"bar\"></sub>" +
				"		<sub type=\"foo\" value=\"bat\"></sub>" +
				"		<sub type=\"bat\" value=\"foo\"></sub>" +
				"	</row>" +
				"	<row id=\"r2\" col1=\"r2c1\"><col2>r2c2</col2><col3>r2c3</col3></row>" +
				"	<row id=\"r3\" col1=\"r3c1\"><col2>r3c2</col2><col3>r3c3</col3></row>" +
				"</table>";
		
		DataElement element = proxy.fromXML(xmlString);
		manager.put("test", "-", element);
		assertTrue(manager.contians("test", "-"));
		assertEquals("table",manager.get("test-name", "-").getAsPrimitive());
		assertEquals("row",  manager.get("test-members-[0]-name", "-").getAsPrimitive());
        assertEquals("r1",   manager.get("test-members-[0]-attrs-id","-").getAsPrimitive());
        assertEquals("r1c1", manager.get("test-members-[0]-attrs-col1","-").getAsPrimitive());
        assertEquals("r1c2", manager.get("test-members-[0]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r1c3", manager.get("test-members-[0]-members-[1]-members-[0]","-").getAsPrimitive());
        assertEquals("sub",  manager.get("test-members-[0]-members-[2]-name","-").getAsPrimitive());
        assertEquals("foo",  manager.get("test-members-[0]-members-[2]-attrs-type","-").getAsPrimitive());
        assertEquals("bar",  manager.get("test-members-[0]-members-[2]-attrs-value","-").getAsPrimitive());
        assertEquals("sub",  manager.get("test-members-[0]-members-[3]-name","-").getAsPrimitive());
        assertEquals("foo",  manager.get("test-members-[0]-members-[3]-attrs-type","-").getAsPrimitive());
        assertEquals("bat",  manager.get("test-members-[0]-members-[3]-attrs-value","-").getAsPrimitive());
        assertEquals("sub",  manager.get("test-members-[0]-members-[4]-name","-").getAsPrimitive());
        assertEquals("bat",  manager.get("test-members-[0]-members-[4]-attrs-type","-").getAsPrimitive());
        assertEquals("foo",  manager.get("test-members-[0]-members-[4]-attrs-value","-").getAsPrimitive());
        assertEquals("r2",   manager.get("test-members-[1]-attrs-id","-").getAsPrimitive());
        assertEquals("r2c1", manager.get("test-members-[1]-attrs-col1","-").getAsPrimitive());
        assertEquals("r2c2", manager.get("test-members-[1]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r2c3", manager.get("test-members-[1]-members-[1]-members-[0]","-").getAsPrimitive());
        assertEquals("r3",   manager.get("test-members-[2]-attrs-id","-").getAsPrimitive());
        assertEquals("r3c1", manager.get("test-members-[2]-attrs-col1","-").getAsPrimitive());
        assertEquals("r3c2", manager.get("test-members-[2]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r3c3", manager.get("test-members-[2]-members-[1]-members-[0]","-").getAsPrimitive());
	}

	@Test
	public void testFromXml4() throws IOException, MergeException {
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
		assertEquals("row",  manager.get("test-members-[0]-name", "-").getAsPrimitive());
        assertEquals("r1",   manager.get("test-members-[0]-attrs-id","-").getAsPrimitive());
        assertEquals("r1c1", manager.get("test-members-[0]-attrs-col1","-").getAsPrimitive());
        assertEquals("r1c2", manager.get("test-members-[0]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r1c3", manager.get("test-members-[0]-members-[1]-members-[0]","-").getAsPrimitive());
        assertEquals("Foo\t",manager.get("test-members-[1]","-").getAsPrimitive());
        assertEquals("r2",   manager.get("test-members-[2]-attrs-id","-").getAsPrimitive());
        assertEquals("r2c1", manager.get("test-members-[2]-attrs-col1","-").getAsPrimitive());
        assertEquals("r2c2", manager.get("test-members-[2]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r2c3", manager.get("test-members-[2]-members-[1]-members-[0]","-").getAsPrimitive());
        assertEquals("Foo\t",manager.get("test-members-[3]","-").getAsPrimitive());
        assertEquals("r3",   manager.get("test-members-[4]-attrs-id","-").getAsPrimitive());
        assertEquals("r3c1", manager.get("test-members-[4]-attrs-col1","-").getAsPrimitive());
        assertEquals("r3c2", manager.get("test-members-[4]-members-[0]-members-[0]","-").getAsPrimitive());
        assertEquals("r3c3", manager.get("test-members-[4]-members-[1]-members-[0]","-").getAsPrimitive());
	}

}
