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
		assertEquals(Replace.OBJECT_ATTRIBUTE_LIST_THROW, replace.getObjectAttrList());
		assertEquals(Replace.OBJECT_ATTRIBUTE_OBJECT_THROW, replace.getObjectAttrObject());
		assertEquals(Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW, replace.getObjectAttrPrimitive());
		assertEquals(Replace.LIST_THROW, replace.getIfList());
		assertEquals(Replace.LIST_ATTR_MISSING_THROW, replace.getListAttrMissing());
		assertEquals(Replace.LIST_ATTR_NOT_PRIMITIVE_THROW, replace.getListAttrNotPrimitive());
		assertEquals(true, replace.getProcessAfter());
	}
	
	@Test
	public void testReplaceWithParms() {
		Replace replace = new Replace("source", "P",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				false);
		
		assertEquals(AbstractDirective.TYPE_REPLACE, replace.getType());
		assertEquals("source", replace.getDataSource());
		assertEquals("P", replace.getDataDelimeter());
		assertEquals(Replace.MISSING_IGNORE, replace.getIfSourceMissing());
		assertEquals(Replace.PRIMITIVE_IGNORE, replace.getIfPrimitive());
		assertEquals(Replace.OBJECT_IGNORE, replace.getIfObject());
		assertEquals(Replace.OBJECT_ATTRIBUTE_LIST_FIRST, replace.getObjectAttrList());
		assertEquals(Replace.OBJECT_ATTRIBUTE_OBJECT_THROW, replace.getObjectAttrObject());
		assertEquals(Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW, replace.getObjectAttrPrimitive());
		assertEquals(Replace.LIST_IGNORE, replace.getIfList());
		assertEquals(Replace.LIST_ATTR_MISSING_THROW, replace.getListAttrMissing());
		assertEquals(Replace.LIST_ATTR_NOT_PRIMITIVE_THROW, replace.getListAttrNotPrimitive());
		assertEquals(false, replace.getProcessAfter());
	}
	
	@Test
	public void testGetMergable() {
		Replace replace = new Replace("source", "P",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				false);

		Replace mergable = (Replace) replace.getMergable();
		assertEquals(AbstractDirective.TYPE_REPLACE, 			mergable.getType());
		assertEquals("source", 									mergable.getDataSource());
		assertEquals("P", 										mergable.getDataDelimeter());
		assertEquals(Replace.MISSING_IGNORE, 					mergable.getIfSourceMissing());
		assertEquals(Replace.PRIMITIVE_IGNORE, 					mergable.getIfPrimitive());
		assertEquals(Replace.OBJECT_IGNORE, 					mergable.getIfObject());
		assertEquals(Replace.OBJECT_ATTRIBUTE_LIST_FIRST, 		mergable.getObjectAttrList());
		assertEquals(Replace.OBJECT_ATTRIBUTE_OBJECT_THROW, 	mergable.getObjectAttrObject());
		assertEquals(Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW, 	mergable.getObjectAttrPrimitive());
		assertEquals(Replace.LIST_IGNORE, 						mergable.getIfList());
		assertEquals(Replace.LIST_ATTR_MISSING_THROW, 			mergable.getListAttrMissing());
		assertEquals(Replace.LIST_ATTR_NOT_PRIMITIVE_THROW, 	mergable.getListAttrNotPrimitive());
		assertEquals(false, 									mergable.getProcessAfter());
	}
	
	@Test
	public void testExecuteMissingThrow() throws MergeException {
		Template template = new Template("test", "missing", "throw", "<foo> - <one>", "<", ">", config );
		Replace directive = new Replace("missing","-",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "replace", "missing", "<foo> - <one>", "<", ">", config);
		Replace directive = new Replace("missing","-",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE, 
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.replace.missing");
		template = context.merge();
		assertEquals("<foo> - <one>", template.getContent());
	}

	@Test
	public void testExecutePrimitiveThrow() throws MergeException {
		Template template = new Template("test", "primitive", "throw", "<foo> - <one>", "<", ">", config);
		Replace directive = new Replace("data.primitive", "-",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_IGNORE, 
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "primitive", "ignore", "<data.primitive> - <one>", "<", ">", config);
		Replace directive = new Replace("data.primitive", "-",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "replace", "primitive", "<foo>", "<", ">", config);
		Replace directive = new Replace("data.object-foo", "-",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_REPLACE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "object", "throw", "<foo> - <one>", "<", ">", config);
		Replace directive = new Replace("data.object", "-",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "replace", "missing", "<foo> - <one>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
	public void testExecuteObjectReplacePrimitiveThrow() throws MergeException {
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

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, config, "test.object.replace");
			context.getMergeData().put("data.object", "-", replaceObject);
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
	}
	
	@Test
	public void testExecuteObjectReplacePrimitiveIgnore() throws MergeException {
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

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_IGNORE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		template = context.merge();
		assertEquals("<A><B><C><D><E><F><G><H><I><J>", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteObjectReplacePrimitiveReplace() throws MergeException {
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

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		template.cleanup(config);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		template = context.merge();
		assertEquals("abcdefghij", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteObjectReplaceObjectThrow() throws MergeException {
		DataObject replaceObject = new DataObject();
		DataList aList = new DataList();
		aList.add(new DataPrimitive("a"));
		aList.add(new DataPrimitive("b"));
		replaceObject.put("A", aList);
		aList = new DataList();
		aList.add(new DataPrimitive("C"));
		replaceObject.put("B", aList);
		DataObject aObject = new DataObject();
		replaceObject.put("C", aObject);

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		try {
			context.merge();
		} catch (MergeException e) {
			return; // expected
		}
		fail("ObjectReplaceObject failed to throw");
	}
	
	@Test
	public void testExecuteObjectReplaceObjectIgnore() throws MergeException {
		DataObject replaceObject = new DataObject();
		DataList aList = new DataList();
		aList.add(new DataPrimitive("a"));
		aList.add(new DataPrimitive("b"));
		replaceObject.put("A", aList);
		aList = new DataList();
		aList.add(new DataPrimitive("C"));
		replaceObject.put("B", aList);
		DataObject aObject = new DataObject();
		replaceObject.put("C", aObject);

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_IGNORE,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		template = context.merge();
		assertEquals("aC<C>", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteObjectReplaceListThrow() throws MergeException {
		DataObject replaceObject = new DataObject();
		DataList aList = new DataList();
		aList.add(new DataPrimitive("a"));
		aList.add(new DataPrimitive("b"));
		replaceObject.put("A", aList);
		aList = new DataList();
		aList.add(new DataPrimitive("C"));
		replaceObject.put("B", aList);
		aList = new DataList();
		replaceObject.put("C", aList);

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		try {
			template = context.merge();
		} catch (MergeException e) {
			return; //expected
		}
		fail("Object-List Throw did not throw");
	}
	
	@Test
	public void testExecuteObjectReplaceListIgnore() throws MergeException {
		DataObject replaceObject = new DataObject();
		DataList aList = new DataList();
		aList.add(new DataPrimitive("a"));
		aList.add(new DataPrimitive("b"));
		replaceObject.put("A", aList);
		aList = new DataList();
		aList.add(new DataPrimitive("C"));
		replaceObject.put("B", aList);
		aList = new DataList();
		replaceObject.put("C", aList);

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_IGNORE,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		template = context.merge();
		assertEquals("<A><B><C>", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteObjectReplaceListFirst() throws MergeException {
		DataObject replaceObject = new DataObject();
		DataList aList = new DataList();
		aList.add(new DataPrimitive("a"));
		aList.add(new DataPrimitive("b"));
		replaceObject.put("A", aList);
		aList = new DataList();
		aList.add(new DataPrimitive("C"));
		replaceObject.put("B", aList);
		aList = new DataList();
		replaceObject.put("C", aList);

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		template = context.merge();
		assertEquals("aC<C>", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteObjectReplaceListLast() throws MergeException {
		DataObject replaceObject = new DataObject();
		DataList aList = new DataList();
		aList.add(new DataPrimitive("a"));
		aList.add(new DataPrimitive("b"));
		replaceObject.put("A", aList);
		aList = new DataList();
		aList.add(new DataPrimitive("C"));
		replaceObject.put("B", aList);
		aList = new DataList();
		replaceObject.put("C", aList);

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_LAST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, config, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		template = context.merge();
		assertEquals("bC<C>", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteListThrow() throws MergeException {
		Template template = new Template("test", "replace", "missing", "content", "<", ">", config);
		Replace directive = new Replace("data.list", "-", 
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "list", "ignore", "<foo> - <one>", "<", ">", config);
		Replace directive = new Replace("data.list", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "replace", "list", "<foo> - <one>", "<", ">", config);
		Replace directive = new Replace("data.list", "-", 
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_REPLACE,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "object", "replace", "<foo> - <one> - <three>", "<", ">", config);
		Replace directive = new Replace("data.default", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				false);
		template.addDirective(directive);
		directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "repeat", "", "<foo parseFirst>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
		Template template = new Template("test", "replace", "parameters", "<foo> - <one>", "<", ">", config);
		Replace directive = new Replace("data.object", "-", 
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW,
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
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
	
	@Test
	public void testSetGetProcessAfter() {
		Replace directive = new Replace();
		directive.setProcessAfter(true);
		assertTrue(directive.getProcessAfter());
	}
	
	@Test
	public void testSetGetObjectAttrPrimitive() {
		for (int i : Replace.OBJECT_ATTRIBUTE_PRIMITIVE_OPTIONS().keySet()) {
			Replace directive = new Replace();
			directive.setObjectAttrPrimitive(i);
			assertEquals(i, directive.getObjectAttrPrimitive());
		}
	}
	
	@Test
	public void testSetGetObjectAttrList() {
		for (int i : Replace.OBJECT_ATTRIBUTE_LIST_OPTIONS().keySet()) {
			Replace directive = new Replace();
			directive.setObjectAttrList(i);
			assertEquals(i, directive.getObjectAttrList());
		}
	}
	
	@Test
	public void testSetGetObjectAttrObject() {
		for (int i : Replace.OBJECT_ATTRIBUTE_OBJECT_OPTIONS().keySet()) {
			Replace directive = new Replace();
			directive.setObjectAttrObject(i);
			assertEquals(i, directive.getObjectAttrObject());
		}
	}

	@Test
	public void testSetGetListAttrNotPrimitive() {
		for (int i : Replace.LIST_ATTR_NOT_PRIMITIVE_OPTIONS().keySet()) {
			Replace directive = new Replace();
			directive.setListAttrNotPrimitive(i);
			assertEquals(i, directive.getListAttrNotPrimitive());
		}
	}

	@Test
	public void testSetGetListAttrMissing() {
		for (int i : Replace.LIST_ATTR_MISSING_OPTIONS().keySet()) {
			Replace directive = new Replace();
			directive.setListAttrMissing(i);
			assertEquals(i, directive.getListAttrMissing());
		}
	}
}
