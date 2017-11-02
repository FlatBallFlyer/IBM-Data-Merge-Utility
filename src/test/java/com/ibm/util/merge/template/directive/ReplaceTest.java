package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.Replace;

public class ReplaceTest {
	private Config config;
	private TemplateCache cache;
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
	}

	@Test
	public void testReplace() {
		Replace replace = new Replace();
		assertEquals(AbstractDirective.TYPE_REPLACE, replace.getType());
		assertEquals("", replace.getDataSource());
		assertEquals("-", replace.getDataDelimeter());
		assertEquals(Replace.MISSING_THROW, replace.getIfSourceMissing());
		assertEquals(Replace.PRIMITIVE_THROW, replace.getIfPrimitive());
		assertEquals(Replace.OBJECT_THROW, replace.getIfObject());
		assertEquals(Replace.LIST_THROW, replace.getIfList());
		assertEquals(true, replace.getProcessAfter());
	}
	
	@Test
	public void testReplaceWithParms() {
		Replace replace = new Replace("source", "P",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.LIST_IGNORE,
				false);
		
		assertEquals(AbstractDirective.TYPE_REPLACE, replace.getType());
		assertEquals("source", replace.getDataSource());
		assertEquals("P", replace.getDataDelimeter());
		assertEquals(Replace.MISSING_IGNORE, replace.getIfSourceMissing());
		assertEquals(Replace.PRIMITIVE_IGNORE, replace.getIfPrimitive());
		assertEquals(Replace.OBJECT_IGNORE, replace.getIfObject());
		assertEquals(Replace.LIST_IGNORE, replace.getIfList());
		assertEquals(false, replace.getProcessAfter());
	}
	
	@Test
	public void testGetMergable() {
		
	}
	
	@Test
	public void testExecuteMissingThrow() throws MergeException {
		Template template = new Template("test", "missing", "throw", "<foo> - <one>", "<", ">" );
		Replace directive = new Replace("missing","-",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.LIST_IGNORE,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, config, "test.missing.throw");
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
		fail("Missing Throw did not throw exception");
	}
	
	@Test
	public void testExecuteMissingSkip() throws MergeException {
		Template template = new Template("test", "replace", "missing", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("missing","-",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE, 
				Replace.LIST_IGNORE,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.replace.missing");
		template = context.merge();
		assertEquals("<foo> - <one>", template.getContent());
	}

	@Test
	public void testExecutePrimitiveThrow() throws MergeException {
		Template template = new Template("test", "primitive", "throw", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.primitive", "-",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_IGNORE, 
				Replace.LIST_IGNORE,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, config, "test.primitive.throw");
			DataPrimitive primitive = new DataPrimitive("test");
			context.getMergeData().put("data.primitive", "-", primitive);
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
		fail("Missing Throw did not throw exception");
	}

	@Test
	public void testExecutePrimitiveIgnore() throws MergeException {
		Template template = new Template("test", "primitive", "ignore", "<data.primitive> - <one>", "<", ">");
		Replace directive = new Replace("data.primitive", "-",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_THROW,
				Replace.LIST_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.primitive.ignore");
		DataPrimitive primitive = new DataPrimitive("test");
		context.getMergeData().put("data.primitive", "-", primitive);
		template = context.merge();
		assertEquals("<data.primitive> - <one>", template.getContent());
	}

	@Test
	public void testExecutePrimitiveReplace() throws MergeException {
		Template template = new Template("test", "replace", "primitive", "<foo>", "<", ">");
		Replace directive = new Replace("data.object-foo", "-",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_REPLACE,
				Replace.OBJECT_THROW,
				Replace.LIST_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.replace.primitive");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("bar"));
		object.put("one", new DataPrimitive("two"));
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals("bar", template.getMergeContent().getValue());
	}
	
	@Test
	public void testExecuteObjectThrow() throws MergeException {
		Template template = new Template("test", "object", "throw", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.object", "-",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_THROW,
				Replace.LIST_IGNORE,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, config, "test.primitive.throw");
			DataObject object = new DataObject();
			object.put("foo", new DataPrimitive("bar"));
			object.put("one", new DataPrimitive("two"));
			context.getMergeData().put("data.object", "-", object);
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
		fail("Missing Throw did not throw exception");
	}

	@Test
	public void testExecuteObjectIgnore() throws MergeException {
		Template template = new Template("test", "replace", "missing", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_IGNORE,
				Replace.LIST_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.replace.missing");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("bar"));
		object.put("one", new DataPrimitive("two"));
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals("<foo> - <one>", template.getContent());
	}
	
	@Test
	public void testExecuteObjectReplace() throws MergeException {
		DataObject replaceObject = new DataObject();
		replaceObject.put("A", new DataPrimitive("a"));
		replaceObject.put("B", new DataPrimitive("b"));
		replaceObject.put("C", new DataPrimitive("c"));
		replaceObject.put("D", new DataPrimitive("d"));
		replaceObject.put("E", new DataPrimitive("e"));
		replaceObject.put("F", new DataPrimitive("f"));
		replaceObject.put("G", new DataPrimitive("g"));
		replaceObject.put("H", new DataPrimitive("h"));
		replaceObject.put("I", new DataPrimitive("i"));
		replaceObject.put("J", new DataPrimitive("j"));

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">");
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.LIST_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		template = context.merge();
		assertEquals("abcdefghij", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteListThrow() throws MergeException {
		Template template = new Template("test", "replace", "missing", "content", "<", ">");
		Replace directive = new Replace("data.list", "-", 
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.LIST_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, config, "test.primitive.throw");
			DataList list = new DataList();
			context.getMergeData().put("data.list", "-", list);
			template = context.merge();
			assertEquals("Some Output", template.getContent());
		} catch (MergeException e) {
			return; // Expected
		}
		fail("Missing Throw did not throw exception");
	}
	
	@Test
	public void testExecuteListIgnore() throws MergeException {
		Template template = new Template("test", "list", "ignore", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.list", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_THROW,
				Replace.LIST_IGNORE,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.list.ignore");
		DataList list = new DataList();
		DataObject obj = new DataObject();
		obj.put("from", new DataPrimitive("foo"));
		obj.put("to", new DataPrimitive("bar"));
		list.add(obj);
		obj.put("from", new DataPrimitive("one"));
		obj.put("to", new DataPrimitive("two"));
		list.add(obj);
		context.getMergeData().put("data.list", "-", list);
		template = context.merge();
		assertEquals("<foo> - <one>", template.getContent());
	}
	
	@Test
	public void testExecuteListReplace() throws MergeException {
		Template template = new Template("test", "replace", "list", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.list", "-", 
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.LIST_REPLACE,
				true);
		directive.setFromAttribute("from");
		directive.setToAttribute("to");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.replace.list");
		DataList list = new DataList();
		DataObject obj = new DataObject();
		obj.put("from", new DataPrimitive("foo"));
		obj.put("to", new DataPrimitive("bar"));
		list.add(obj);
		obj = new DataObject();
		obj.put("from", new DataPrimitive("one"));
		obj.put("to", new DataPrimitive("two"));
		list.add(obj);
		context.getMergeData().put("data.list", "-", list);
		template = context.merge();
		assertEquals("bar - two", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteNoProcess() throws MergeException {
		Template template = new Template("test", "object", "replace", "<foo> - <one> - <three>", "<", ">");
		Replace directive = new Replace("data.default", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.LIST_THROW,
				false);
		template.addDirective(directive);
		directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.LIST_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("bar"));
		object.put("one", new DataPrimitive("two"));
		object.put("three", new DataPrimitive("four"));
		context.getMergeData().put("data.default", "-", object);
		object = new DataObject();
		object.put("foo", new DataPrimitive("fog"));
		object.put("one", new DataPrimitive("bog"));
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals("fog - bog - four", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteRepeatTwo() throws MergeException {
		Template template = new Template("test", "repeat", "", "<foo parseFirst>", "<", ">");
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.LIST_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		config.setNestLimit(4);
		Merger context = new Merger(cache, config, "test.repeat.");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("<two parseFirst>"));
		object.put("fooone", new DataPrimitive("<bar parseFirst>"));
		object.put("two", new DataPrimitive("rab"));
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals(4, template.getContext().getConfig().getNestLimit());
		assertEquals("rab", template.getMergedOutput().getValue());
	}
	
	@Test // Parameter Object
	public void testExecuteObjectOfStringList() throws MergeException {
		Template template = new Template("test", "replace", "parameters", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.LIST_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.replace.parameters");
		DataObject object = new DataObject();
		DataList array = new DataList();
		array.add(new DataPrimitive("bar"));
		object.put("foo", array);
		array = new DataList();
		array.add(new DataPrimitive("two"));
		array.add(new DataPrimitive("three"));
		object.put("one", array);
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals("bar - two", template.getMergedOutput().getValue());
	}

	@Test
	public void testGetSetDataSource() {
		Replace directive = new Replace();
		directive.setDataSource("foo");
		assertEquals("foo",directive.getDataSource());
	}

	@Test
	public void testGetSetToAttribute() {
		Replace directive = new Replace();
		directive.setToAttribute("foo");
		assertEquals("foo",directive.getToAttribute());
	}

	@Test
	public void testSetGetFromAttribute() {
		Replace directive = new Replace();
		directive.setFromAttribute("foo");
		assertEquals("foo",directive.getFromAttribute());
	}

}
