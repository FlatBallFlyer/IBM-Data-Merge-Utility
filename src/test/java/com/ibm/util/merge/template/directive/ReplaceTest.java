package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.Replace;

public class ReplaceTest {
	private Cache cache;
	private static final DataProxyJson parser = new DataProxyJson(false);
	
	@Before
	public void setUp() throws Exception {
		cache = new Cache();
	}

	@Test
	public void testReplace() throws MergeException {
		Replace replace = new Replace();
		assertEquals(AbstractDirective.TYPE_REPLACE, replace.getType());
		assertEquals("", replace.getDataSource());
		assertEquals("-", replace.getDataDelimeter());
		assertEquals(Replace.MISSING_IGNORE, replace.getIfSourceMissing());
		assertEquals(Replace.PRIMITIVE_IGNORE, replace.getIfPrimitive());
		assertEquals(Replace.OBJECT_IGNORE, replace.getIfObject());
		assertEquals(Replace.OBJECT_ATTRIBUTE_LIST_THROW, replace.getObjectAttrList());
		assertEquals(Replace.OBJECT_ATTRIBUTE_OBJECT_THROW, replace.getObjectAttrObject());
		assertEquals(Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW, replace.getObjectAttrPrimitive());
		assertEquals(Replace.LIST_IGNORE, replace.getIfList());
		assertEquals(Replace.LIST_ATTR_MISSING_THROW, replace.getListAttrMissing());
		assertEquals(Replace.LIST_ATTR_NOT_PRIMITIVE_THROW, replace.getListAttrNotPrimitive());
		assertEquals(false, replace.getProcessAfter());
		assertEquals(false, replace.getProcessRequire());
	}
	
	@Test
	public void testReplaceWithParms() throws MergeException {
		Replace replace = new Replace("source", "P",  "",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				false, false);
		
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
		assertEquals(false, replace.getProcessRequire());
	}
	
	@Test
	public void testGetMergable() throws MergeException {
		Replace replace = new Replace("source", "P",  "",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				false, true);

		Merger context = new Merger(new Cache(), "system.sample.");
		replace.cachePrepare(new Template(), new Config());
		Replace mergable = (Replace) replace.getMergable(context);
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
	public void testExecuteNoOp() throws MergeException {
		Cache cache = new Cache();
		Template template = new Template("test","noop","", "Simple Test");
		Replace directive = new Replace();
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger merger = new Merger(cache, "test.noop.");
		template = merger.merge();
		assertEquals("Simple Test", template.getMergedOutput().getValue());
	}
	

	@Test
	public void testExecuteMissingThrow() throws MergeException {
		Template template = new Template("test", "missing", "throw", "<foo> - <one>", "<", ">" );
		Replace directive = new Replace("missing","-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, "test.missing.throw");
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
		fail("Missing Throw did not throw exception");
	}
	
	@Test
	public void testExecuteMissingSkip() throws MergeException {
		Template template = new Template("test", "replace", "missing", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("missing","-", "",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE, 
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.missing");
		template = context.merge();
		assertEquals("<foo> - <one>", template.getContent());
	}

	@Test
	public void testExecuteMissingReplace() throws MergeException {
		Template template = new Template("test", "replace", "missing", "<missing>", "<", ">");
		Replace directive = new Replace("missing","-",  "newValue",
				Replace.MISSING_REPLACE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE, 
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.missing");
		template = context.merge();
		assertEquals("newValue", template.getMergeContent().getValue());
	}

	@Test
	public void testExecutePrimitiveThrow() throws MergeException {
		Template template = new Template("test", "primitive", "throw", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.primitive", "-", "",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_IGNORE, 
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, "test.primitive.throw");
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
		Replace directive = new Replace("data.primitive", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.primitive.ignore");
		DataPrimitive primitive = new DataPrimitive("test");
		context.getMergeData().put("data.primitive", "-", primitive);
		template = context.merge();
		assertEquals("<data.primitive> - <one>", template.getContent());
	}

	@Test
	public void testExecutePrimitiveReplace() throws MergeException {
		Template template = new Template("test", "replace", "primitive", "<foo>", "<", ">");
		Replace directive = new Replace("data.object-foo", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_REPLACE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.primitive");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("bar"));
		object.put("one", new DataPrimitive("two"));
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals("bar", template.getMergeContent().getValue());
	}
	
	@Test
	public void testExecutePrimitiveReplaceOverride() throws MergeException {
		Template template = new Template("test", "replace", "primitive", "<one>", "<", ">");
		Replace directive = new Replace("data.object-foo", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_REPLACE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		directive.setFromValue("one");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.primitive");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("bar"));
		object.put("one", new DataPrimitive("two"));
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals("bar", template.getMergeContent().getValue());
	}
	
	@Test
	public void testExecutePrimitiveJson() throws MergeException {
		Template template = new Template("test", "replace", "primitive", "<data>", "<", ">");
		template.setContentEncoding(TagSegment.ENCODE_NONE);
		Replace directive = new Replace("data", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_JSON,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.primitive");
		DataPrimitive primitive = new DataPrimitive("bar");
		context.getMergeData().put("data", "-", primitive);
		template = context.merge();
		assertEquals("\"bar\"", template.getMergeContent().getValue());
	}
	
	@Test
	public void testExecuteObjectThrow() throws MergeException {
		Template template = new Template("test", "object", "throw", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, "test.primitive.throw");
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
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.missing");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("bar"));
		object.put("one", new DataPrimitive("two"));
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals("<foo> - <one>", template.getContent());
	}
	
	@Test
	public void testReplaceObjectList() throws MergeException {
		Template template = new Template("test", "object", "json", "<foo>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE_LIST,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		directive.setFromAttribute("from");
		directive.setToAttribute("to");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.json");
		DataObject obj = new DataObject();
		obj.put("from", new DataPrimitive("foo"));
		obj.put("to", new DataPrimitive("bar"));
		context.getMergeData().put("data.object", "-", obj);
		String output = context.merge().getMergeContent().getValue();
		assertEquals("bar", output);
	}
	
	@Test
	public void testReplaceObjectJson() throws MergeException {
		Template template = new Template("test", "object", "json", "<data.object>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_REPLACE_JSON,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.json");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("bar"));
		object.put("one", new DataPrimitive("two"));
		context.getMergeData().put("data.object", "-", object);
		String output = context.merge().getMergeContent().getValue();
		DataElement reply = parser.fromString(output, "", null);
		assertTrue(reply.isObject());
		assertEquals("two", reply.getAsObject().get("one").getAsPrimitive());
		assertEquals("bar", reply.getAsObject().get("foo").getAsPrimitive());
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

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, "test.object.replace");
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

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_IGNORE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
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

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
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

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
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

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_IGNORE,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
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

		Template template = new Template("test", "object", "replace", "<A><B><C><D><E><F><G><H><I><J>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
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

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_IGNORE, 
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
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

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
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

		Template template = new Template("test", "object", "replace", "<A><B><C>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_LAST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
		context.getMergeData().put("data.object", "-", replaceObject);
		template = context.merge();
		assertEquals("bC<C>", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteListThrow() throws MergeException {
		Template template = new Template("test", "replace", "missing", "content", "<", ">");
		Replace directive = new Replace("data.list", "-", "",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, "test.primitive.throw");
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
		Replace directive = new Replace("data.list", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_IGNORE, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.list.ignore");
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
		Replace directive = new Replace("data.list", "-", "",
				Replace.MISSING_IGNORE,
				Replace.PRIMITIVE_IGNORE,
				Replace.OBJECT_IGNORE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_REPLACE, "from", "to",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.list");
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
	public void testReplaceListFirst() throws MergeException {
		DataObject replaceObj1 = new DataObject();
		replaceObj1.put("A", new DataPrimitive("a"));
		replaceObj1.put("B", new DataPrimitive("b"));
		DataObject replaceObj2 = new DataObject();
		replaceObj2.put("C", new DataPrimitive("c"));
		replaceObj2.put("D", new DataPrimitive("d"));
		DataList list = new DataList();
		list.add(replaceObj1);
		list.add(replaceObj2);

		Template template = new Template("test", "list", "first", "<A><B><C><D>", "<", ">");
		Replace directive = new Replace("data.list", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_FIRST, "from", "to",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.list.first");
		context.getMergeData().put("data.list", "-", list);
		template = context.merge();
		assertEquals("ab<C><D>", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testReplaceListLast() throws MergeException {
		DataObject replaceObj1 = new DataObject();
		replaceObj1.put("A", new DataPrimitive("a"));
		replaceObj1.put("B", new DataPrimitive("b"));
		DataObject replaceObj2 = new DataObject();
		replaceObj2.put("C", new DataPrimitive("c"));
		replaceObj2.put("D", new DataPrimitive("d"));
		DataList list = new DataList();
		list.add(replaceObj1);
		list.add(replaceObj2);

		Template template = new Template("test", "list", "first", "<A><B><C><D>", "<", ">");
		Replace directive = new Replace("data.list", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_LAST, "from", "to", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.list.first");
		context.getMergeData().put("data.list", "-", list);
		template = context.merge();
		assertEquals("<A><B>cd", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testReplaceListJson() throws MergeException {
		DataObject replaceObj1 = new DataObject();
		replaceObj1.put("A", new DataPrimitive("a"));
		replaceObj1.put("B", new DataPrimitive("b"));
		DataObject replaceObj2 = new DataObject();
		replaceObj2.put("C", new DataPrimitive("c"));
		replaceObj2.put("D", new DataPrimitive("d"));
		DataList list = new DataList();
		list.add(replaceObj1);
		list.add(replaceObj2);

		Template template = new Template("test", "list", "json", "<data.list>", "<", ">");
		Replace directive = new Replace("data.list", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_JSON, "", "", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.list.json");
		context.getMergeData().put("data.list", "-", list);
		template = context.merge();
		String output = template.getMergedOutput().getValue();
		DataElement reply = parser.fromString(output, "", null);
		assertTrue(reply.isList());
		assertTrue(reply.getAsList().get(0).isObject());
		DataObject obj = reply.getAsList().get(0).getAsObject();
		assertEquals("a", obj.get("A").getAsPrimitive());
		assertEquals("b", obj.get("B").getAsPrimitive());
		assertTrue(reply.getAsList().get(1).isObject());
		obj = reply.getAsList().get(1).getAsObject();
		assertEquals("c", obj.get("C").getAsPrimitive());
		assertEquals("d", obj.get("D").getAsPrimitive());
	}
	
	@Test
	public void testExecuteNoProcess() throws MergeException {
		Template template = new Template("test", "object", "replace", "<foo> - <one> - <three>", "<", ">");
		Replace directive = new Replace("data.default", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				false, false);
		template.addDirective(directive);
		directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.replace");
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
		Config config = new Config("{\"nestLimit\":\"4\"}");
		Cache cache = new Cache(config); 
		Template template = new Template("test", "repeat", "", "<foo parseFirst>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.repeat.");
		DataObject object = new DataObject();
		object.put("foo", new DataPrimitive("<two parseFirst>"));
		object.put("fooone", new DataPrimitive("<bar parseFirst>"));
		object.put("two", new DataPrimitive("rab"));
		context.getMergeData().put("data.object", "-", object);
		template = context.merge();
		assertEquals(4, config.getNestLimit());
		assertEquals("rab", template.getMergedOutput().getValue());
	}
	
	@Test // Parameter Object
	public void testExecuteObjectOfStringList() throws MergeException {
		Template template = new Template("test", "replace", "parameters", "<foo> - <one>", "<", ">");
		Replace directive = new Replace("data.object", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.parameters");
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
	public void testExecuteRequire() throws MergeException {
		Template template = new Template("test", "replace", "require", "<foo>", "<", ">");
		Replace directive = new Replace("data", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_REPLACE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_FIRST,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "",
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, true);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.replace.require");
		context.getMergeData().put("data", "-", new DataPrimitive("foo"));
		try {
			String answer = context.merge().getMergeContent().getValue();
			assertEquals("<foo>", answer);
		} catch (MergeException e) {
			return; // expected exception
		}
		fail("Required failed to throw");
	}
	
	@Test
	public void testGetSetDataSource() throws MergeException {
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

	@Test
	public void testSetGetRequired() {
		Replace replace = new Replace();
		assertFalse(replace.getProcessRequire());
		replace.setProcessRequire(true);
		assertTrue(replace.getProcessRequire());
	}
}
