package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.AbstractDirective;
import com.ibm.util.merge.template.directive.Insert;

public class InsertTest {
	private Cache cache;
	private String bkm1 = "{bookmark=\"bkm1\" group=\"test\" template=\"child\" varyby=\"type\"}";
	private String bkm2 = "{bookmark=\"bkm2\" group=\"test\" template=\"child\" }";
	private String bkm3 = "{bookmark=\"bkm3\" group=\"test\" template=\"sub\" }";
	private DataList arrayOfObjects;
	private DataList arrayOfVaryBy;
	private DataObject simpleObject;
	private HashSet<String> emptyList = new HashSet<String>(); 
	
	@Before
	public void setUp() throws Exception {
		cache = new Cache();
		
		simpleObject = new DataObject();
		simpleObject.put("col1", new DataPrimitive("val1"));
		simpleObject.put("col2", new DataPrimitive("val2"));
		
		arrayOfObjects = new DataList();
		DataObject row = new DataObject();
		row.put("col1", new DataPrimitive("row1val1"));
		row.put("col2", new DataPrimitive("row1val2"));
		arrayOfObjects.add(row);
		row = new DataObject();
		row.put("col1", new DataPrimitive("row2val1"));
		row.put("col2", new DataPrimitive("row2val2"));
		arrayOfObjects.add(row);
		
		arrayOfVaryBy = new DataList();
		row = new DataObject();
		row.put("col1", new DataPrimitive("row1val1"));
		row.put("col2", new DataPrimitive("row1val2"));
		row.put("type", new DataPrimitive("T1"));
		arrayOfVaryBy.add(row);
		row = new DataObject();
		row.put("col1", new DataPrimitive("row2val1"));
		row.put("col2", new DataPrimitive("row2val2"));
		row.put("type", new DataPrimitive("T2"));
		arrayOfVaryBy.add(row);
		row = new DataObject();
		row.put("col1", new DataPrimitive("row3val1"));
		row.put("col2", new DataPrimitive("row3val2"));
		row.put("col3", new DataPrimitive("extraT"));
		row.put("type", new DataPrimitive("T3"));
		arrayOfVaryBy.add(row);
	}

	@Test
	public void testInsert() throws MergeException {
		Insert directive = new Insert();
		assertEquals(AbstractDirective.TYPE_INSERT, directive.getType());
		assertEquals("", directive.getDataSource());
		assertEquals("-", directive.getDataDelimeter());
		assertEquals(Insert.MISSING_IGNORE, directive.getIfSourceMissing());
		assertEquals(Insert.PRIMITIVE_IGNORE, directive.getIfPrimitive());
		assertEquals(Insert.OBJECT_IGNORE, directive.getIfObject());
		assertEquals(Insert.LIST_IGNORE, directive.getIfList());
		assertEquals(".*", directive.getBookmarkPattern());
		assertTrue(directive.getNotFirst().isEmpty());
		assertTrue(directive.getNotLast().isEmpty());
		assertTrue(directive.getOnlyFirst().isEmpty());
		assertTrue(directive.getOnlyLast().isEmpty());
	}

	@Test 
	public void testExecuteNoOp() throws MergeException {
		Cache cache = new Cache();
		Template template = new Template("test","noop","", "Simple Test");
		Insert directive = new Insert();
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger merger = new Merger(cache, "test.noop.");
		template = merger.merge();
		assertEquals("Simple Test", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testInsertRecursionLimit() throws MergeException {
		Config config = new Config("{\"nestLimit\":\"5\"}");
		Cache cache = new Cache(config);
		Template template = new Template("test", "child", "Template Content ", this.bkm2, "{", "}" );
		Insert directive = new Insert("data.primitive","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_INSERT,
				Insert.OBJECT_THROW,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		
		try {
			Merger context = new Merger(cache, "test.child.");
			context.getMergeData().put("data.primitive", "-", new DataPrimitive("Foo"));
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
		fail("Fail - Recursion Limit - did not throw exception - infinite loop");
	}

	@Test
	public void testInsertListFirst() throws MergeException {
		Template template = new Template("test", "parent", "", this.bkm2, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_INSERT_FIRST,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		template = new Template("test", "child", "", "Child Content {col1} {col2}");
		Replace replace = new Replace("idmuContext", "-", "",
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
		template.addDirective(replace);
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.list", "-", this.arrayOfObjects);
		template = context.merge();
		assertEquals("Child Content row1val1 row1val2", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testInsertListLast() throws MergeException {
		Template template = new Template("test", "parent", "", this.bkm2, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_INSERT_LAST,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		template = new Template("test", "child", "", "Child Content {col1} {col2}");
		Replace replace = new Replace("idmuContext", "-",  "",
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
		template.addDirective(replace);
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.list", "-", this.arrayOfObjects);
		template = context.merge();
		assertEquals("Child Content row2val1 row2val2", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testInsertSourceDelimeterMissingPrimitiveObjectList() throws MergeException {
		HashSet<String> list = new HashSet<String>();
		list.add("One"); list.add("two"); list.add("three");
		Insert directive = new Insert("source", "delim",  
				Insert.MISSING_INSERT, 
				Insert.PRIMITIVE_INSERT, 
				Insert.OBJECT_INSERT_LIST, 
				Insert.LIST_INSERT,
				list, list, list, list,
				"foo",Insert.INSERT_IF_STRING_EQUALS,"");
		assertEquals(AbstractDirective.TYPE_INSERT, directive.getType());
		assertEquals("source", directive.getDataSource());
		assertEquals("delim", directive.getDataDelimeter());
		assertEquals(Insert.MISSING_INSERT, directive.getIfSourceMissing());
		assertEquals(Insert.PRIMITIVE_INSERT, directive.getIfPrimitive());
		assertEquals(Insert.OBJECT_INSERT_LIST, directive.getIfObject());
		assertEquals(Insert.LIST_INSERT, directive.getIfList());
		assertEquals("foo", directive.getBookmarkPattern());
		assertEquals(3, directive.getNotFirst().size());
		assertEquals(3, directive.getNotLast().size());
		assertEquals(3, directive.getOnlyFirst().size());
		assertEquals(3, directive.getOnlyLast().size());
	}

	@Test
	public void testGetMergable() throws MergeException {
		HashSet<String> empty = new HashSet<String>();
		Insert directive = new Insert("","-", 
			Insert.MISSING_IGNORE, 
			Insert.PRIMITIVE_IGNORE, 
			Insert.OBJECT_IGNORE,
			Insert.LIST_IGNORE, 
			empty, empty, empty, empty,
			".*",Insert.INSERT_IF_STRING_EQUALS,"");

		Merger context = new Merger(new Cache(), "system.sample.");
		directive.cachePrepare(new Template(), new Config());
		Insert mergable = directive.getMergable(context);
		assertNotSame(directive, mergable);
		assertEquals(directive.getType(), AbstractDirective.TYPE_INSERT);
		assertEquals(directive.getName(), mergable.getName());
		assertEquals(directive.getDataSource(), mergable.getDataSource());
		assertEquals(directive.getDataDelimeter(), mergable.getDataDelimeter());
		assertEquals(directive.getIfList(), mergable.getIfList());
		assertEquals(directive.getIfPrimitive(), mergable.getIfPrimitive());
		assertEquals(directive.getIfObject(), mergable.getIfObject());
		assertEquals(directive.getIfSourceMissing(), mergable.getIfSourceMissing());
		assertEquals(directive.getBookmarkPattern(), mergable.getBookmarkPattern());
		assertTrue(mergable.getNotFirst().containsAll(directive.getNotFirst()));
		assertTrue(mergable.getNotLast().containsAll(directive.getNotLast()));
		assertTrue(mergable.getOnlyFirst().containsAll(directive.getOnlyFirst()));
		assertTrue(mergable.getOnlyLast().containsAll(directive.getOnlyLast()));
	}
	
	@Test
	public void testMissingThrow() throws MergeException {
		Template template = new Template("test", "missing", "throw", this.bkm1, "{", "}");
		Insert directive = new Insert("missing","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_IGNORE,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
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
	public void testMissingIgnore() throws MergeException {
		Template template = new Template("test", "missing", "ignore", this.bkm2, "{", "}" );
		Insert directive = new Insert("missing","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_THROW,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.missing.ignore");
		template = context.merge();
		assertEquals(this.bkm2, template.getContent());
	}

	@Test
	public void testMissingInsert() throws MergeException {
		Template template = new Template("test", "missing", "ignore", this.bkm2, "{", "}" );
		Insert directive = new Insert("missing","-",
				Insert.MISSING_INSERT,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_THROW,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		cache.postTemplate(new Template("test", "child", "", "Child Content"));
		Merger context = new Merger(cache, "test.missing.ignore");
		template = context.merge();
		assertEquals("Child Content", template.getMergedOutput().getValue());
	}

	@Test
	public void testPrimitiveThrow() throws MergeException {
		Template template = new Template("test", "primitive", "throw", this.bkm2, "{", "}" );
		Insert directive = new Insert("data.primitive","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_IGNORE,
				Insert.LIST_IGNORE,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, "test.primitive.throw");
			context.getMergeData().put("data.primitive", "-", new DataPrimitive("Foo"));
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
		fail("Primitive Throw did not throw exception");
	}

	@Test
	public void testPrimitiveIgnore() throws MergeException {
		Template template = new Template("test", "primitive", "ignore", this.bkm2, "{", "}" );
		Insert directive = new Insert("data.primitive","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_THROW,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.primitive.ignore");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("Foo"));
		template = context.merge();
		assertEquals(this.bkm2, template.getContent());
	}

	@Test
	public void testPrimitiveInsert() throws MergeException {
		Template template = new Template("test", "primitive", "insert", this.bkm2, "{", "}" );
		Insert directive = new Insert("data.primitive","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_INSERT,
				Insert.OBJECT_THROW,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		
		template = new Template("test", "child", "", "Child - {idmuContext}");
		Replace replace = new Replace("idmuContext", "-",  "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_REPLACE,
				Replace.OBJECT_THROW,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_THROW,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(replace);
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.primitive.insert");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("Foo"));
		template = context.merge();
		assertEquals("Child - Foo", template.getMergeContent().getValue());
	}

	@Test
	public void testObjectThrow() throws MergeException {
		Template template = new Template("test", "object", "throw", "bkm1", "{", "}");
		Insert directive = new Insert("data.object","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_THROW,
				Insert.LIST_IGNORE,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, "test.object.throw");
			context.getMergeData().put("data.object", "-", new DataObject());
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
		fail("Missing Throw did not throw exception");
	}

	@Test
	public void testObjectIgnore() throws MergeException {
		Template template = new Template("test", "object", "ignore", this.bkm1, "{", "}");
		Insert directive = new Insert("data.object","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_IGNORE,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.object.ignore");
		context.getMergeData().put("data.object", "-", new DataObject());
		template = context.merge();
		assertEquals(this.bkm1, template.getContent());
	}

	@Test
	public void testObjectInsertObject() throws MergeException {
		Template template = new Template("test", "object", "insertObject", this.bkm2, "{", "}");
		Insert directive = new Insert("data.object","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_INSERT_OBJECT,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		
		template = new Template("test", "child", "", "Child {attribute}, {value} - ");
		Replace replace = new Replace("idmuContext", "-",  "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(replace);
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.object.insertObject");
		context.getMergeData().put("data.object", "-", this.simpleObject);
		template = context.merge();
		assertEquals("Child col1, val1 - Child col2, val2 - ", template.getMergeContent().getValue());
	}

	@Test
	public void testObjectInsertList() throws MergeException {
		Template template = new Template("test", "object", "insertObject", this.bkm2, "{", "}");
		Insert directive = new Insert("data.object","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_INSERT_LIST,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		
		template = new Template("test", "child", "", "Child {col1}, {col2} - ");
		Replace replace = new Replace("idmuContext", "-",  "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(replace);
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.object.insertObject");
		context.getMergeData().put("data.object", "-", this.simpleObject);
		template = context.merge();
		assertEquals("Child val1, val2 - ", template.getMergeContent().getValue());
	}

	@Test
	public void testListThrow() throws MergeException {
		Template template = new Template("test", "list", "throw", this.bkm2, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		try {
			Merger context = new Merger(cache, "test.list.throw");
			context.getMergeData().put("data.list", "-", new DataList());
			context.merge();
		} catch (MergeException e) {
			return; // Expected
		}
		fail("List Throw did not throw exception");
	}

	@Test
	public void testListIgnore() throws MergeException {
		Template template = new Template("test", "list", "ignore", this.bkm2, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_THROW,
				Insert.LIST_IGNORE,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.list.ignore");
		context.getMergeData().put("data.list", "-", new DataList());
		template = context.merge();
		assertEquals(this.bkm2, template.getContent());
	}

	@Test
	public void testListInsert() throws MergeException {
		Template template = new Template("test", "parent", "", this.bkm2, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_INSERT,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		template = new Template("test", "child", "", "Child Content, ");
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.list", "-", this.arrayOfObjects);
		template = context.merge();
		assertEquals("Child Content, Child Content, ", template.getMergedOutput().getValue());
	}

	@Test
	public void testListInsertIf() throws MergeException {
		Template template = new Template("test", "parent", "", this.bkm2, "{", "}");
		Insert directive = new Insert("data.primitive","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_INSERT_IF,
				Insert.OBJECT_IGNORE,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",
				Insert.INSERT_IF_STRING_EQUALS,"bar");

		template.addDirective(directive);
		cache.postTemplate(template);
		template = new Template("test", "child", "", "Inserted!");
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("bar"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());

		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("foo"));
		template = context.merge();
		assertEquals("", template.getMergedOutput().getValue());
		
		directive.setIfOperator(Insert.INSERT_IF_STRING_LT);
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("foo"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());

		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("bam"));
		template = context.merge();
		assertEquals("", template.getMergedOutput().getValue());

		directive.setIfOperator(Insert.INSERT_IF_STRING_GT);
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("foo"));
		template = context.merge();
		assertEquals("", template.getMergedOutput().getValue());

		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("bam"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());
		
		directive.setIfOperator(Insert.INSERT_IF_STRING_EMPTY);
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("foo"));
		template = context.merge();
		assertEquals("", template.getMergedOutput().getValue());

		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive(""));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());

		directive.setIfOperator(Insert.INSERT_IF_STRING_NOT_EMPTY);
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("foo"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());

		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive(""));
		template = context.merge();
		assertEquals("", template.getMergedOutput().getValue());

		directive.setIfValue("200.25");
		directive.setIfOperator(Insert.INSERT_IF_VALUE_EQUALS);
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("200.14"));
		template = context.merge();
		assertEquals("", template.getMergedOutput().getValue());

		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("200.25"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());
		
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("00200.25"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());
		
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("  0200.25000"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());

		directive.setIfOperator(Insert.INSERT_IF_VALUE_GT);
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("200.14"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());

		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("201.14"));
		template = context.merge();
		assertEquals("", template.getMergedOutput().getValue());

		directive.setIfOperator(Insert.INSERT_IF_VALUE_LT);
		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("200.14"));
		template = context.merge();
		assertEquals("", template.getMergedOutput().getValue());

		context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("201.14"));
		template = context.merge();
		assertEquals("Inserted!", template.getMergedOutput().getValue());
	}

	@Test
	public void testListInsertNested() throws MergeException {
		Template template = new Template("test", "parent", "", "Root-" + this.bkm2, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_INSERT,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		template = new Template("test", "child", "", "Child " + this.bkm3 + "Content, ");
		template.addDirective(directive);
		cache.postTemplate(template);
		template = new Template("test", "sub", "", "final ");
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.list", "-", this.arrayOfObjects);
		template = context.merge();
		assertEquals("Root-Child final final Content, Child final final Content, ", template.getMergedOutput().getValue());
	}

	@Test
	public void testListInsertTable() throws MergeException {
		Template template = new Template("test", "table", "", "<table>{bookmark=\"bkm\" group=\"test\" template=\"row\"}</table>", "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_INSERT,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);

		template = new Template("test", "row", "", "<row>{bookmark=\"bkm2\" group=\"test\" template=\"col\" }</row>");
		directive = new Insert("idmuContext", "-", 
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_INSERT_OBJECT,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		
		template = new Template("test", "col", "", "<col>{value}</col>");
		Replace replace = new Replace("idmuContext", "-",  "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(replace);
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.table.");
		context.getMergeData().put("data.list", "-", this.arrayOfObjects);
		template = context.merge();
		assertEquals("<table><row><col>row1val1</col><col>row1val2</col></row><row><col>row2val1</col><col>row2val2</col></row></table>", template.getMergedOutput().getValue());
	}

	@Test
	public void testListInsertMissingVaryBy() throws MergeException {
		Template template = new Template("test", "parent", "", this.bkm1, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_INSERT,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		cache.postTemplate(new Template("test", "child", "T1", "Child T1 Content, "));
		cache.postTemplate(new Template("test", "child", "T2", "Child T2 Content, "));
		cache.postTemplate(new Template("test", "child", "", "Child Default Content, "));
		
		Merger context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.list", "-", this.arrayOfObjects);
		try {
			template = context.merge();
		} catch (MergeException e) {
			return; // expected
		}
		fail("missing vary by attribute failed to throw execption");
	}

	@Test
	public void testListInsertVaryBy() throws MergeException {
		Template template = new Template("test", "parent", "", this.bkm1, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_IGNORE,
				Insert.PRIMITIVE_IGNORE,
				Insert.OBJECT_IGNORE,
				Insert.LIST_INSERT,
				emptyList, emptyList, emptyList, emptyList,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		cache.postTemplate(new Template("test", "child", "T1", "Child T1 Content, "));
		cache.postTemplate(new Template("test", "child", "T2", "Child T2 Content, "));
		cache.postTemplate(new Template("test", "child", "", "Child Default Content, "));
		
		Merger context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.list", "-", this.arrayOfVaryBy);
		template = context.merge();
		assertEquals("Child T1 Content, Child T2 Content, Child Default Content, ", template.getMergedOutput().getValue());
	}

	@Test
	public void testListInsertNotOnlyFirstLast() throws MergeException {
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
		
		HashSet<String> notFirst  = new HashSet<String>(); notFirst.add( "A");  notFirst.add("E");
		HashSet<String> notLast   = new HashSet<String>(); notLast.add(  "B");   notLast.add("F");
		HashSet<String> onlyFirst = new HashSet<String>(); onlyFirst.add("C"); onlyFirst.add("G");
		HashSet<String> onlyLast  = new HashSet<String>(); onlyLast.add( "D");  onlyLast.add("H");
		
		Template template = new Template("test", "list", "notfirstlast", this.bkm2, "{", "}");
		Replace replace = new Replace("data.replace", "-", "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(replace);
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_THROW,
				Insert.LIST_INSERT,
				notFirst, notLast, onlyFirst, onlyLast,
				".*",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		cache.postTemplate(template);
		
		template = new Template("test", "child", "", "Child {col1}, {col2}{A}{B}{C}{D}{E}{F}{G}{H}{I}{J} - ");
		replace = new Replace("idmuContext", "-",  "",
				Replace.MISSING_THROW,
				Replace.PRIMITIVE_THROW,
				Replace.OBJECT_REPLACE,
				Replace.OBJECT_ATTRIBUTE_PRIMITIVE_REPLACE,
				Replace.OBJECT_ATTRIBUTE_LIST_THROW,
				Replace.OBJECT_ATTRIBUTE_OBJECT_THROW,
				Replace.LIST_THROW, "", "", 
				Replace.LIST_ATTR_MISSING_THROW,
				Replace.LIST_ATTR_NOT_PRIMITIVE_THROW,
				true, false);
		template.addDirective(replace);
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.list.notfirstlast");
		context.getMergeData().put("data.list", "-", this.arrayOfVaryBy);
		context.getMergeData().put("data.replace", "-", replaceObject);
		template = context.merge();
		assertEquals("Child row1val1, row1val2bcfgij - Child row2val1, row2val2abefij - Child row3val1, row3val2adehij - ", template.getMergeContent().getValue());
	}

	@Test
	public void testListInsertBookmarkSelection() throws MergeException {
		Template template = new Template("test", "parent", "", this.bkm1 + this.bkm2, "{", "}");
		Insert directive = new Insert("data.list","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_THROW,
				Insert.OBJECT_THROW,
				Insert.LIST_INSERT,
				emptyList, emptyList, emptyList, emptyList,
				"bkm1",Insert.INSERT_IF_STRING_EQUALS,"");

		template.addDirective(directive);
		directive = new Insert("data.primitive","-",
				Insert.MISSING_THROW,
				Insert.PRIMITIVE_INSERT,
				Insert.OBJECT_THROW,
				Insert.LIST_THROW,
				emptyList, emptyList, emptyList, emptyList,
				"bkm2",Insert.INSERT_IF_STRING_EQUALS,"");
		template.addDirective(directive);
		cache.postTemplate(template);
		cache.postTemplate(new Template("test", "child", "T1", "Child T1 Content, "));
		cache.postTemplate(new Template("test", "child", "T2", "Child T2 Content, "));
		cache.postTemplate(new Template("test", "child", "", "Child Default Content, "));
		
		Merger context = new Merger(cache, "test.parent.");
		context.getMergeData().put("data.list", "-", this.arrayOfVaryBy);
		context.getMergeData().put("data.primitive", "-", new DataPrimitive("Foo"));
		template = context.merge();
		assertEquals("Child T1 Content, Child T2 Content, Child Default Content, Child Default Content, ", template.getMergedOutput().getValue());
	}

	@Test
	public void testSetGetNotFirst() {
		HashSet<String> list = new HashSet<String>(); list.add("A"); list.add("B");		
		Insert directive = new Insert();
		directive.setNotFirst(list);
		assertTrue(directive.getNotFirst().containsAll(list));
		assertTrue(list.containsAll(directive.getNotFirst()));
	}

	@Test
	public void testSetGetNotLast() {
		HashSet<String> list = new HashSet<String>(); list.add("A"); list.add("B");		
		Insert directive = new Insert();
		directive.setNotLast(list);
		assertTrue(directive.getNotLast().containsAll(list));
		assertTrue(list.containsAll(directive.getNotLast()));
	}

	@Test
	public void testSetGetOnlyFirst() {
		HashSet<String> list = new HashSet<String>(); list.add("A"); list.add("B");		
		Insert directive = new Insert();
		directive.setOnlyFirst(list);
		assertTrue(directive.getOnlyFirst().containsAll(list));
		assertTrue(list.containsAll(directive.getOnlyFirst()));
	}

	@Test
	public void testSetGetOnlyLast() {
		HashSet<String> list = new HashSet<String>(); list.add("A"); list.add("B");		
		Insert directive = new Insert();
		directive.setOnlyLast(list);
		assertTrue(directive.getOnlyLast().containsAll(list));
		assertTrue(list.containsAll(directive.getOnlyLast()));
	}

	@Test
	public void testSetGetBookmarkPattern() {
		Insert directive = new Insert();
		directive.setBookmarkPattern("Foo");
		assertEquals("Foo",directive.getBookmarkPattern());
	}

	@Test
	public void testSetGetRemoveBookmarks() {
		Insert directive = new Insert();
		directive.setBookmarkPattern("Foo");
		assertEquals("Foo",directive.getBookmarkPattern());
	}

}
