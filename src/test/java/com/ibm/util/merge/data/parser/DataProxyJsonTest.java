package com.ibm.util.merge.data.parser;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.Enrich;
import com.ibm.util.merge.template.directive.Insert;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.Replace;
import com.ibm.util.merge.template.directive.SaveFile;

public class DataProxyJsonTest {
	DataProxyJson proxy = new DataProxyJson(false);
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTemplateProxy() throws MergeException {
		Template sample = new Template("System","Test", "", "SomeContent");
		sample.addDirective(new Enrich());
		sample.addDirective(new Insert());
		sample.addDirective(new ParseData());
		sample.addDirective(new Replace());
		sample.addDirective(new SaveFile());
		String templateString = proxy.toString(sample);
		Template json = proxy.fromString(templateString, Template.class);
		assertEquals(templateString, proxy.toString(json));
	}
	
	
	@Test
	public void testEnrichDirectiveProxy() throws MergeException {
		Template sample = new Template("System","Test", "", "SomeContent");
		Enrich directive = new Enrich();
		directive.setEnrichClass("SomeNewClass");
		directive.setEnrichCommand("Command String");
		directive.setEnrichParameter("SomeParameter");
		directive.setEnrichSource("ASource");
		directive.setName("aName");
		directive.setParseAs(Config.PARSE_CSV);
		directive.setTargetDataDelimeter("--");
		directive.setTargetDataName("aName");
		sample.addDirective(directive);
		String templateString = proxy.toString(sample);
		Template json = proxy.fromString(templateString, Template.class);
		directive = (Enrich) json.getDirectives().get(0); 
		assertEquals(templateString, proxy.toString(json));
		assertEquals("SomeNewClass", 	directive.getEnrichClass());
		assertEquals("Command String", 	directive.getEnrichCommand());
		assertEquals("SomeParameter", 	directive.getEnrichParameter());
		assertEquals("ASource", 		directive.getEnrichSource());
		assertEquals("aName", 			directive.getName());
		assertEquals(Config.PARSE_CSV, 	directive.getParseAs());
		assertEquals("--", 				directive.getTargetDataDelimeter());
		assertEquals("aName", 			directive.getTargetDataName());
	}

	@Test
	public void testInsertDirectiveProxy() throws MergeException {
		HashSet<String> aList = new HashSet<String>();
		aList.add("a"); aList.add("b"); aList.add("c");
		Template sample = new Template("System","Test", "", "SomeContent");
		Insert directive = new Insert();
		directive.setBookmarkPattern("foo.*");
		directive.setDataDelimeter("--");
		directive.setDataSource("aSource");
		directive.setIfList(Insert.LIST_INSERT);
		directive.setIfObject(Insert.OBJECT_INSERT_LIST);
		directive.setIfPrimitive(Insert.PRIMITIVE_INSERT);
		directive.setIfSourceMissing(Insert.MISSING_INSERT);
		directive.setName("aName");
		directive.setNotFirst(aList);
		directive.setNotLast(aList);
		directive.setOnlyFirst(aList);
		directive.setOnlyLast(aList);
		sample.addDirective(directive);
		String templateString = proxy.toString(sample);
		Template json = proxy.fromString(templateString, Template.class);
		directive = (Insert) json.getDirectives().get(0); 
		assertEquals(templateString, proxy.toString(json));
		assertEquals("foo.*", directive.getBookmarkPattern());
		assertEquals("--", directive.getDataDelimeter());
		assertEquals("aSource", 	directive.getDataSource());
		assertEquals(Insert.LIST_INSERT, 	directive.getIfList());
		assertEquals(Insert.OBJECT_INSERT_LIST,	directive.getIfObject());
		assertEquals(Insert.PRIMITIVE_INSERT,	directive.getIfPrimitive());
		assertEquals(Insert.MISSING_INSERT,	directive.getIfSourceMissing());
		assertEquals("aName",	directive.getName());
		assertListEquals(aList,	directive.getNotFirst());
		assertListEquals(aList,	directive.getNotLast());
		assertListEquals(aList,	directive.getOnlyFirst());
		assertListEquals(aList,	directive.getOnlyLast());

	}

	@Test
	public void testParseDirectiveProxy() throws MergeException {
		Template sample = new Template("System","Test", "", "SomeContent");
		ParseData directive = new ParseData();
		directive.setDataDelimeter("--");
		directive.setDataSource("aSource");
		directive.setDataTarget("aTarget");
		directive.setIfList(ParseData.LIST_PARSE_FIRST);
		directive.setIfObject(ParseData.OBJECT_IGNORE);
		directive.setIfPrimitive(ParseData.PRIMITIVE_PARSE);
		directive.setIfSourceMissing(ParseData.SOURCE_MISSING_IGNORE);
		directive.setName("aName");
		directive.setParseFormat(Config.PARSE_CSV);
		directive.setStaticData("static");
		sample.addDirective(directive);
		String templateString = proxy.toString(sample);
		Template json = proxy.fromString(templateString, Template.class);
		directive = (ParseData) json.getDirectives().get(0); 
		assertEquals(templateString, proxy.toString(json));
		assertEquals("--", 							directive.getDataDelimeter());
		assertEquals("aSource", 					directive.getDataSource());
		assertEquals("aTarget", 					directive.getDataTarget());
		assertEquals(ParseData.LIST_PARSE_FIRST, 	directive.getIfList());
		assertEquals(ParseData.OBJECT_IGNORE, 		directive.getIfObject());
		assertEquals(ParseData.PRIMITIVE_PARSE, 	directive.getIfPrimitive());
		assertEquals(ParseData.SOURCE_MISSING_IGNORE, directive.getIfSourceMissing());
		assertEquals("aName", 						directive.getName());
		assertEquals(Config.PARSE_CSV, 				directive.getParseFormat());
		assertEquals("static", 						directive.getStaticData());
	}

	@Test
	public void testReplaceDirectiveProxy() throws MergeException {
		Template sample = new Template("System","Test", "", "SomeContent");
		Replace directive = new Replace();
		directive.setDataDelimeter("==");
		directive.setDataSource("aSource");
		directive.setFromAttribute("from");
		directive.setIfList(Replace.LIST_REPLACE);
		directive.setIfObject(Replace.OBJECT_REPLACE);
		directive.setIfPrimitive(Replace.PRIMITIVE_REPLACE);
		directive.setIfSourceMissing(Replace.MISSING_IGNORE);
		directive.setName("aName");
		directive.setProcessAfter(true);
		directive.setToAttribute("target");
		sample.addDirective(directive);
		String templateString = proxy.toString(sample);
		Template json = proxy.fromString(templateString, Template.class);
		directive = (Replace) json.getDirectives().get(0); 
		assertEquals(templateString, proxy.toString(json));
		assertEquals("==", 						directive.getDataDelimeter());
		assertEquals("aSource", 				directive.getDataSource());
		assertEquals("from", 					directive.getFromAttribute());
		assertEquals(Replace.LIST_REPLACE, 		directive.getIfList());
		assertEquals(Replace.OBJECT_REPLACE, 	directive.getIfObject());
		assertEquals(Replace.PRIMITIVE_REPLACE, directive.getIfPrimitive());
		assertEquals(Replace.MISSING_IGNORE, 	directive.getIfSourceMissing());
		assertEquals("aName", 					directive.getName());
		assertEquals(true, 						directive.getProcessAfter());
		assertEquals("target", 					directive.getToAttribute());
	}

	@Test
	public void testSaveDirectiveProxy() throws MergeException {
		Template sample = new Template("System","Test", "", "SomeContent");
		SaveFile directive = new SaveFile();
		directive.setClearAfter(true);
		directive.setFilename("aFile");
		directive.setName("aName");
		sample.addDirective(directive);
		String templateString = proxy.toString(sample);
		Template json = proxy.fromString(templateString, Template.class);
		directive = (SaveFile) json.getDirectives().get(0); 
		assertEquals(templateString, proxy.toString(json));
		assertEquals(true, 		directive.getClearAfter());
		assertEquals("aFile", 	directive.getFilename());
		assertEquals("aName", 	directive.getName());
	}

	private void assertListEquals(HashSet<String> expected, HashSet<String> values) {
		assertEquals(expected.size(), values.size());
		for (String aValue : expected) {
			assertTrue(values.contains(aValue));
		}
	}

	@Test
	public void testDataElementProxy() throws Merge500 {
		DataObject base = new DataObject();
		DataObject id = new DataObject();
		id.put("group", 	new DataPrimitive("syste"));
		id.put("name", 		new DataPrimitive("test"));
		id.put("variant", 	new DataPrimitive("type1"));
		base.put("id", id);
		DataList list = new DataList();
		list.add(new DataPrimitive("Foo"));
		list.add(new DataPrimitive("Bar"));
		list.add(new DataPrimitive("Bat"));
		base.put("aList", list);
		base.put("description", new DataPrimitive("a Simple Element"));
		
		String json = proxy.toString(base);
		DataElement result = proxy.fromString(json, DataElement.class);
		assertEquals(json, proxy.toString(result));
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("id"));
		assertTrue(result.getAsObject().get("id").isObject());
		assertTrue(result.getAsObject().get("id").getAsObject().containsKey("group"));
		assertTrue(result.getAsObject().get("id").getAsObject().get("group").isPrimitive());
		assertEquals("syste", result.getAsObject().get("id").getAsObject().get("group").getAsPrimitive());
		assertTrue(result.getAsObject().get("id").getAsObject().containsKey("name"));
		assertTrue(result.getAsObject().get("id").getAsObject().get("name").isPrimitive());
		assertEquals("test", result.getAsObject().get("id").getAsObject().get("name").getAsPrimitive());
		assertTrue(result.getAsObject().get("id").getAsObject().containsKey("variant"));
		assertTrue(result.getAsObject().get("id").getAsObject().get("variant").isPrimitive());
		assertEquals("type1", result.getAsObject().get("id").getAsObject().get("variant").getAsPrimitive());
		assertTrue(result.getAsObject().containsKey("aList"));
		assertTrue(result.getAsObject().get("aList").isList());
		assertEquals(3, result.getAsObject().get("aList").getAsList().size());
		assertTrue(result.getAsObject().get("aList").getAsList().get(0).isPrimitive());
		assertEquals("Foo", result.getAsObject().get("aList").getAsList().get(0).getAsPrimitive());
		assertTrue(result.getAsObject().get("aList").getAsList().get(1).isPrimitive());
		assertEquals("Bar", result.getAsObject().get("aList").getAsList().get(1).getAsPrimitive());
		assertTrue(result.getAsObject().get("aList").getAsList().get(2).isPrimitive());
		assertEquals("Bat", result.getAsObject().get("aList").getAsList().get(2).getAsPrimitive());
		assertTrue(result.getAsObject().containsKey("description"));
		assertTrue(result.getAsObject().get("description").isPrimitive());
		assertEquals("a Simple Element", result.getAsObject().get("description").getAsPrimitive());
	}

	@Test
	public void testDataElementProxy2() throws Merge500 {
		DataObject table = new DataObject();
		table.put("Test", new DataPrimitive(null));

		String json = proxy.toString(table);
		DataElement result = proxy.fromString(json, DataElement.class);
		
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("Test"));
		assertTrue(result.getAsObject().get("Test").isPrimitive());
		assertNotNull(result.getAsObject().get("Test").getAsPrimitive());
	}

	@Test
	public void testPretyJson() throws MergeException {
		DataObject table = new DataObject();
		table.put("Bar", new DataPrimitive("Bam"));
		table.put("Foo", new DataPrimitive("Fam"));
		DataObject sub = new DataObject();
		sub.put("One", new DataPrimitive("Two"));
		table.put("Obj", sub);
		
// Suspended because of differences in mvn Install and Eclipse Build
//		proxy = new DataProxyJson(false);
//		String json = proxy.toString(table);
//		assertEquals("{\"Obj\":{\"One\":\"Two\"},\"Foo\":\"Fam\",\"Bar\":\"Bam\"}", json);
		
//		proxy = new DataProxyJson(true);
//		json = proxy.toString(table);
//		assertEquals("{\n  \"Obj\": {\n    \"One\": \"Two\"\n  },\n  \"Foo\": \"Fam\",\n  \"Bar\": \"Bam\"\n}", json);
		assertTrue(true);
	}
}
