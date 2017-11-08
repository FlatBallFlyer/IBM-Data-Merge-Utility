package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.Parser;
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
	private ParseData directive;
	DataObject sourceObject;
	DataList sourceList1;
	DataList sourceList2;
	DataList sourceList3;
	private String jsonString1 = "[{\"col1\":\"r1c1\",\"col2\":\"r1c2\",\"col3\":\"r1c3\"},{\"col1\":\"r2c1\",\"col2\":\"r2c2\",\"col3\":\"r2c3\"}]";
	private String jsonString2 = "{\"name\":\"foo\",\"age\":\"12\"}";
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		template = new Template("system", "test", "", "test content", config);
		directive = new ParseData();
		directive.setName("name");
		directive.setDataSource("test");
		directive.setDataDelimeter("-");
		directive.setDataTarget("target");
		directive.setDataDelimeter("-");
		template.addDirective(directive);
		cache.postTemplate(template);
		merger = new Merger(cache, config, "system.test.");
		
		sourceObject = new DataObject();
		sourceObject.put("Attr1", new DataPrimitive(jsonString1));
		sourceObject.put("Attr2", new DataPrimitive("Test 2"));
		sourceObject.put("Attr3", new DataPrimitive("Test 3"));
		sourceObject.put("Attr4", new DataPrimitive("Test 4"));
		sourceObject.put("Attr5", new DataPrimitive(jsonString2));
		
		sourceList1 = new DataList();
		sourceList1.add(new DataPrimitive(jsonString1));
		sourceList1.add(new DataPrimitive("Test 2"));
		sourceList1.add(new DataPrimitive("Test 3"));
		sourceList1.add(new DataPrimitive("Test 4"));
		sourceList1.add(new DataPrimitive(jsonString2));
		
		sourceList2 = new DataList();
		sourceList2.add(new DataList());
		sourceList2.add(new DataPrimitive(jsonString2));
		sourceList2.add(new DataObject());
		sourceList2.add(new DataPrimitive("Test 2"));
		sourceList2.add(new DataPrimitive(jsonString1));

		sourceList3 = new DataList();
		sourceList3.add(new DataList());
		sourceList3.add(new DataObject());
		sourceList3.add(new DataList());
		sourceList3.add(new DataObject());
		sourceList3.add(new DataList());
	}
	
	@Test
	public void testParseData() throws MergeException {
		assertEquals("name", directive.getName());
		assertEquals(AbstractDirective.TYPE_PARSE, directive.getType());
		assertEquals("test", directive.getDataSource());
		assertEquals("-", directive.getDataDelimeter());
		assertEquals("target", directive.getDataTarget());
		assertEquals("\"", directive.getDataTargetDelimiter());
		assertEquals(Parser.PARSE_CSV, directive.getParseFormat());
		assertEquals(ParseData.LIST_THROW, directive.getIfList());
		assertEquals(ParseData.OBJECT_THROW, directive.getIfObject());
		assertEquals(ParseData.PRIMITIVE_THROW, directive.getIfPrimitive());
		assertEquals(ParseData.SOURCE_MISSING_THROW, directive.getIfSourceMissing());
	}

	@Test
	public void testGetMergable() throws MergeException {
		ParseData mergable = (ParseData) directive.getMergable();
		assertNotSame(mergable, directive);
		assertEquals(mergable.getName(), directive.getName());
		assertEquals(mergable.getType(), directive.getType());
		assertEquals(mergable.getDataSource(), directive.getDataSource());
		assertEquals(mergable.getDataDelimeter(), directive.getDataDelimeter());
		assertEquals(mergable.getDataTarget(), directive.getDataTarget());
		assertEquals(mergable.getDataTargetDelimiter(), directive.getDataTargetDelimiter());
		assertEquals(mergable.getParseFormat(), directive.getParseFormat());
		assertEquals(mergable.getIfList(), directive.getIfList());
		assertEquals(mergable.getIfObject(), directive.getIfObject());
		assertEquals(mergable.getIfPrimitive(), directive.getIfPrimitive());
		assertEquals(mergable.getIfSourceMissing(), directive.getIfSourceMissing());
	}

	@Test
	public void testExecuteSourceMissingThrow() throws MergeException {
		directive.setIfSourceMissing(ParseData.SOURCE_MISSING_THROW);
		template = template.getMergable(merger);
		try {
			template.getMergedOutput();
		} catch (MergeException e) {
			return; // Expected exception
		}
		fail("Missing Throw did not throw");
		assertFalse(merger.getMergeData().contians("target", "-"));
	}
	
	@Test
	public void testExecuteSourceMissingIgnore() throws MergeException {
		directive.setIfSourceMissing(ParseData.SOURCE_MISSING_IGNORE);
		template = template.getMergable(merger);
		template.getMergedOutput();
		assertFalse(merger.getMergeData().contians("target", "-"));
	}
	
	@Test
	public void testExecutePrimitiveThrow() throws MergeException {
		directive.setIfPrimitive(ParseData.PRIMITIVE_THROW);
		merger.getMergeData().put("test", "-", jsonString1);
		template = template.getMergable(merger);
		try {
			template.getMergedOutput();
		} catch (MergeException e) {
			return; // Expected exception
		}
		fail("Missing Throw did not throw");
	}
	
	@Test
	public void testExecutePrimitiveIgnore() throws MergeException {
		directive.setIfPrimitive(ParseData.PRIMITIVE_IGNORE);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", jsonString1);
		template.getMergedOutput();
		assertFalse(merger.getMergeData().contians("target", "-"));
	}
	
	@Test
	public void testExecutePrimitiveParse() throws MergeException {
		directive.setIfPrimitive(ParseData.PRIMITIVE_PARSE);
		directive.setParseFormat(Parser.PARSE_JSON);
		directive.setStaticData(jsonString1);
		template = template.getMergable(merger);
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertTableValue(result);
	}
	
	@Test
	public void testExecutePrimitiveParseStatic() throws MergeException {
		directive.setIfPrimitive(ParseData.PRIMITIVE_PARSE);
		directive.setParseFormat(Parser.PARSE_JSON);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", jsonString1);
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertTableValue(result);
	}
	
	@Test
	public void testExecuteObjectThrow() throws MergeException {
		directive.setIfObject(ParseData.OBJECT_THROW);
		merger.getMergeData().put("test", "-", this.sourceObject);
		template = template.getMergable(merger);
		try {
			template.getMergedOutput();
		} catch (MergeException e) {
			return; // Expected exception
		}
		fail("Missing Throw did not throw");
	}
	
	@Test
	public void testExecuteObjectIgnore() throws MergeException {
		directive.setIfObject(ParseData.OBJECT_IGNORE);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", this.sourceObject);
		template.getMergedOutput();
		assertFalse(merger.getMergeData().contians("target", "-"));
	}
	
	@Test
	public void testExecuteListThrow() throws MergeException {
		directive.setIfList(ParseData.LIST_THROW);
		merger.getMergeData().put("test", "-", this.sourceObject);
		template = template.getMergable(merger);
		try {
			template.getMergedOutput();
		} catch (MergeException e) {
			return; // Expected exception
		}
		fail("Missing Throw did not throw");
	}
	
	@Test
	public void testExecuteListIgnore() throws MergeException {
		directive.setIfList(ParseData.LIST_IGNORE);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", this.sourceList1);
		template.getMergedOutput();
		assertFalse(merger.getMergeData().contians("target", "-"));
	}
	
	@Test
	public void testExecuteListParseFirst1() throws MergeException {
		directive.setIfList(ParseData.LIST_PARSE_FIRST);
		directive.setParseFormat(Parser.PARSE_JSON);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceList1);
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertTableValue(result);
	}
	
	@Test
	public void testExecuteListParseFirst2() throws MergeException {
		directive.setIfList(ParseData.LIST_PARSE_FIRST);
		directive.setParseFormat(Parser.PARSE_JSON);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceList2);
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertObjectValue(result);
	}
	
	@Test
	public void testExecuteListParseFirst3() throws MergeException {
		directive.setIfList(ParseData.LIST_PARSE_FIRST);
		directive.setParseFormat(Parser.PARSE_JSON);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceList3);
		try {
			template.getMergedOutput();
		} catch (MergeException e) {
			return; // Expected exception
		}
		fail("Object Parse First didn't throw on no-primitive");
	}
	
	@Test
	public void testExecuteListParseLast1() throws MergeException {
		directive.setIfList(ParseData.LIST_PARSE_LAST);
		directive.setParseFormat(Parser.PARSE_JSON);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceList1);
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertObjectValue(result);
	}
	
	@Test
	public void testExecuteListParseLast2() throws MergeException {
		directive.setIfList(ParseData.LIST_PARSE_LAST);
		directive.setParseFormat(Parser.PARSE_JSON);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceList2);
		template.getMergedOutput();
		DataElement result = merger.getMergeData().get("target", "-");
		assertTableValue(result);
	}
	
	@Test
	public void testExecuteListParseLast3() throws MergeException {
		directive.setIfList(ParseData.LIST_PARSE_LAST);
		directive.setParseFormat(Parser.PARSE_JSON);
		template = template.getMergable(merger);
		merger.getMergeData().put("test", "-", sourceList3);
		try {
			template.getMergedOutput();
		} catch (MergeException e) {
			return; // Expected exception
		}
		fail("Object Parse Last didn't throw on no-primitive");
	}
	
	private void assertTableValue(DataElement result) throws Merge500 {
		assertTrue(result.isList());
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

	private void assertObjectValue(DataElement result) throws Merge500 {
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("name"));
		assertTrue(result.getAsObject().containsKey("age"));
	}

	@Test
	public void testGetSetDataSource() {
		directive.setDataSource("Foo");
		assertEquals("Foo", directive.getDataSource());
	}

	@Test
	public void testGetSetDataDelimiter() {
		directive.setDataDelimeter("Foo");
		assertEquals("Foo", directive.getDataDelimeter());
	}

	@Test
	public void testGetSetSourceMissing() {
		for (int value : ParseData.MISSING_OPTIONS().keySet()) {
			directive.setIfSourceMissing(value);
			assertEquals(value, directive.getIfSourceMissing());
		}
	}

	@Test
	public void testGetSetIfPrimitive() {
		for (int value : ParseData.PRIMITIVE_OPTIONS().keySet()) {
			directive.setIfPrimitive(value);
			assertEquals(value, directive.getIfPrimitive());
		}
	}

	@Test
	public void testGetSetIfObject() {
		for (int value : ParseData.OBJECT_OPTIONS().keySet()) {
			directive.setIfObject(value);
			assertEquals(value, directive.getIfObject());
		}
	}

	@Test
	public void testGetSetIfList() {
		for (int value : ParseData.LIST_OPTIONS().keySet()) {
			directive.setIfList(value);
			assertEquals(value, directive.getIfList());
		}
	}

	@Test
	public void testGetSetDataTarget() {
		directive.setDataTarget("Foo");
		assertEquals("Foo", directive.getDataTarget());
	}

	@Test
	public void testGetSetDataTargetDelimiter() {
		directive.setDataDelimeter("Foo");
		assertEquals("Foo", directive.getDataDelimeter());
	}

	@Test
	public void testGetSetParseFormat() {
		for (int format : Parser.PARSE_OPTIONS().keySet()) {
			directive.setParseFormat(format);
			assertEquals(format, directive.getParseFormat());
		}
		directive.setParseFormat(Parser.PARSE_CSV);
		assertEquals(Parser.PARSE_CSV, directive.getParseFormat());
		directive.setParseFormat(99);
		assertEquals(Parser.PARSE_CSV, directive.getParseFormat());
	}

}
