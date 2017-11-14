package com.ibm.util.merge.data.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.parser.DataProxyCsv;
import com.ibm.util.merge.exception.MergeException;

public class DataProxyCsvTest {
	private DataProxyCsv cson = new DataProxyCsv();
	
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testCsvJsonProxy() {
		assertTrue(cson instanceof DataProxyCsv);
	}

	@Test
	public void testFromCsv() throws MergeException {
		DataElement csvTable = cson.fromString("col1,col2,col3\nr1c1,r1c2,r1c3\nr2c1,r2c2,r2c3");
		assertTrue(csvTable.isList());
		DataObject row = csvTable.getAsList().get(0).getAsObject(); 
		assertTrue(row.isObject());
		assertEquals("r1c1", row.getAsObject().get("col1").getAsPrimitive());
		assertEquals("r1c2", row.getAsObject().get("col2").getAsPrimitive());
		assertEquals("r1c3", row.getAsObject().get("col3").getAsPrimitive());
		row = csvTable.getAsList().get(1).getAsObject();
		assertTrue(row.isObject());
		assertEquals("r2c1", row.getAsObject().get("col1").getAsPrimitive());
		assertEquals("r2c2", row.getAsObject().get("col2").getAsPrimitive());
		assertEquals("r2c3", row.getAsObject().get("col3").getAsPrimitive());
	}
}
