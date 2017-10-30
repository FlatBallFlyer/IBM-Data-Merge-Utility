package com.ibm.util.merge.data.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.parser.DataProxyHtml;
import com.ibm.util.merge.exception.Merge500;

public class DataProxyHtmlTest {
	DataProxyHtml proxy = new DataProxyHtml();

	@Test
	public void testHtmlJsonProxy() {
		assertTrue(proxy instanceof DataProxyHtml);
	}

	@Test
	public void testFromHTML() throws Merge500 {
		DataElement data = proxy.fromHTML(
				"<html><head></head><body><table>" +
				"<th><td>col1</td><td>col2</td><td>col3</td></th>" +
				"<tr><td>r1c1</td><td>r1c2</td><td>r1c3</td></tr>" +
				"<tr><td>r2c1</td><td>r2c2</td><td>r2c3</td></tr>" +
				"</table>"
				);
		assertTrue(data.isList());
		assertEquals(2, data.getAsList().size());
		DataObject row = data.getAsList().get(0).getAsObject(); 
		assertTrue(row.isObject());
		assertEquals("r1c1", row.getAsObject().get("col1").getAsPrimitive());
		assertEquals("r1c2", row.getAsObject().get("col2").getAsPrimitive());
		assertEquals("r1c3", row.getAsObject().get("col3").getAsPrimitive());
		row = data.getAsList().get(1).getAsObject();
		assertTrue(row.isObject());
		assertEquals("r2c1", row.getAsObject().get("col1").getAsPrimitive());
		assertEquals("r2c2", row.getAsObject().get("col2").getAsPrimitive());
		assertEquals("r2c3", row.getAsObject().get("col3").getAsPrimitive());
	}

}
