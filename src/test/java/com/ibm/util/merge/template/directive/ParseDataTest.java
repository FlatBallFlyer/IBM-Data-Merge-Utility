package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.AbstractDirective;
import com.ibm.util.merge.template.directive.ParseData;

public class ParseDataTest {
	private Config config;
	private TemplateCache cache;
	private Merger merger;
	private Template template;
	private DataProxyJson gson = new DataProxyJson();
	private ParseData directive;
	private HashMap<Integer,String> sourceData;
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		merger = new Merger(cache, config, "system.test.");
		directive = new ParseData();
		
		sourceData = new HashMap<Integer,String>();
		sourceData.put(ParseData.PARSE_CSV, "col1,col2,col3\nr1c1,r1c2,r1c3\nr2c1,r2c2,r2c3");
//TODO		sourceData.put(AbstractDirective.PARSE_HTML, "<html><head></head><body><table><tr><td>....");
		sourceData.put(ParseData.PARSE_JSON, "[{\"col1\":\"r1c1\",\"col2\":\"r1c2\",\"col3\":\"r1c3\"},{\"col1\":\"r2c1\",\"col2\":\"r2c2\",\"col3\":\"r2c3\"}]");
//TODO		sourceData.put(AbstractDirective.PARSE_XML, "<list><row col1=\"r1c1\" col2=\"r1c2\" col3=\"r1c3\"/><row col1=\"r2c1\" col2=\"r2c2\" col3=\"r2c3\"/></list>");
	}
	
	@Test
	public void testGetMergable() throws MergeException {
		ParseData mergable = (ParseData) directive.getMergable();
		assertNotSame(mergable, directive);
		assertEquals(directive.getParseFormat(), mergable.getParseFormat());
		assertEquals(directive.getTargetData(), mergable.getTargetData());
		assertEquals(mergable.getType(), AbstractDirective.TYPE_PARSE);
		assertEquals(null, mergable.getTemplate());
	}

	@Test
	public void testExecuteJson() throws MergeException {
		template = getTestTemplate(ParseData.PARSE_JSON);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceData.get(ParseData.PARSE_JSON));
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertTableValue(result);
	}
	
	@Test
	public void testExecuteCsv() throws MergeException {
		template = getTestTemplate(ParseData.PARSE_CSV);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceData.get(ParseData.PARSE_CSV));
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertTableValue(result);
	}

	@Test
	public void testExecuteXml() throws MergeException {
		fail("Not yet implemented");
		template = getTestTemplate(ParseData.PARSE_XML_DATA);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceData.get(ParseData.PARSE_XML_DATA));
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertTableValue(result);
	}

	@Test
	public void testExecuteHtml() throws MergeException {
		fail("Not yet implemented");
		template = getTestTemplate(ParseData.PARSE_HTML);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceData.get(ParseData.PARSE_HTML));
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertTableValue(result);
	}

	private Template getTestTemplate(int parseJson) {
		Template template = gson.fromJSON(cache.getTemplate("system.test."), Template.class);
		directive.setDataSource("test");
		directive.setTargetData("target");
		directive.setParseFormat(parseJson);
		template.addDirective(directive);
		return template;
	}
	private void assertTableValue(DataElement result) throws Merge500 {
		assertEquals(2, result.getAsList().size());
		DataObject row = result.getAsList().get(0).getAsObject();
		assertTrue(row.containsKey("col1"));
		assertTrue(row.containsKey("col3"));
		assertTrue(row.containsKey("col3"));
		assertEquals("r1c1", row.get("col1").getAsPrimitive());
		assertEquals("r1c2", row.get("col2").getAsPrimitive());
		assertEquals("r1c3", row.get("col3").getAsPrimitive());
		row = result.getAsList().get(1).getAsObject();
		assertTrue(row.containsKey("col1"));
		assertTrue(row.containsKey("col3"));
		assertTrue(row.containsKey("col3"));
		assertEquals("r2c1", row.get("col1").getAsPrimitive());
		assertEquals("r2c2", row.get("col2").getAsPrimitive());
		assertEquals("r2c3", row.get("col3").getAsPrimitive());
	}

	@Test
	public void testGetSetSourceData() {
		directive.setDataSource("Foo");
		assertEquals("Foo", directive.getDataSource());
	}

	@Test
	public void testGetSetTargetData() {
		directive.setTargetData("Foo");
		assertEquals("Foo", directive.getTargetData());
	}

	@Test
	public void testGetSetParseFormat() {
		for (int format : ParseData.PARSE_OPTIONS().keySet()) {
			directive.setParseFormat(format);
			assertEquals(format, directive.getParseFormat());
		}
		directive.setParseFormat(ParseData.PARSE_CSV);
		assertEquals(ParseData.PARSE_CSV, directive.getParseFormat());
		directive.setParseFormat(99);
		assertEquals(ParseData.PARSE_CSV, directive.getParseFormat());
	}

}
