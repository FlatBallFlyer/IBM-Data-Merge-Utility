package com.ibm.util.merge.data.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.Enrich;
import com.ibm.util.merge.template.directive.Insert;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.Replace;
import com.ibm.util.merge.template.directive.SaveFile;

public class DataProxyJsonTest {
	DataProxyJson proxy = new DataProxyJson();
	Config config;
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
	}

	@Test
	public void testTemplateProxy() throws MergeException {
		Template sample = new Template("System","Test", "", "SomeContent");
		sample.addDirective(new Enrich());
		sample.addDirective(new Insert());
		sample.addDirective(new ParseData());
		sample.addDirective(new Replace());
		sample.addDirective(new SaveFile());
		String templateString = proxy.toJson(sample);
		Template json = proxy.fromJSON(templateString, Template.class);
		assertEquals(templateString, proxy.toJson(json));
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
		
		String json = proxy.toJson(base);
		DataElement result = proxy.fromJSON(json, DataElement.class);
		assertEquals(json, proxy.toJson(result));
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
}
