package com.ibm.util.merge.data.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ibm.util.merge.data.DataElement;
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
				"<html><head></head><body>"+
					"<a href='http://example.com/'><b>example</b></a>" +
				"</body></html>",
				"a"
				);
		assertTrue(data.isList());
		assertEquals(1, data.getAsList().size());
		assertEquals("a", data.getAsList().get(0).getAsObject().get("name").getAsPrimitive());
		assertEquals("example", data.getAsList().get(0).getAsObject().get("text").getAsPrimitive());
		assertEquals("<b>example</b>", data.getAsList().get(0).getAsObject().get("html").getAsPrimitive());
		assertEquals("http://example.com/", data.getAsList().get(0).getAsObject().get("attrs").getAsObject().get("href").getAsPrimitive());
	}

}
