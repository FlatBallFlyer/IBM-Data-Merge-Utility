package com.ibm.util.merge.template;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.*;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.*;

public class TemplateTest {
	private Template template;
	
	@Before
	public void setUp() throws Exception {
		template = new Template("System","Test","");
		template.setContent("Test Content");
		template.setDescription("A testing template");
		template.setContentDisposition("attachment;filename=\"FileToReturn.json\"");
		template.setContentEncoding(TagSegment.ENCODE_JSON);
		template.setContentType("application/json");
		template.setContentRedirectUrl("Redirect");
		template.setWrapper("<", ">");
	}

	@Test
	public void testTemplate() throws MergeException {
		template = new Template();
		assertEquals("void",template.getId().group);
		assertEquals("void",template.getId().name);
		assertEquals("void",template.getId().variant);
		assertEquals(Template.STATE_RAW, template.getState());
	}

	@Test
	public void testTemplateStringStringString() throws MergeException {
		template = new Template("System","Test","");
		assertEquals("System",template.getId().group);
		assertEquals("Test",template.getId().name);
		assertTrue(template.getId().variant.isEmpty());
		assertEquals(Template.STATE_RAW, template.getState());
	}

	@Test
	public void testTemplateStringStringStringString() throws MergeException {
		template = new Template("System","Test", "", "SomeContent");
		assertEquals("System",template.getId().group);
		assertEquals("Test",template.getId().name);
		assertTrue(template.getId().variant.isEmpty());
		assertEquals("SomeContent", template.getContent());
		assertEquals(Template.STATE_RAW, template.getState());
	}

	@Test
	public void testTemplateTemplateId() throws MergeException {
		template = new Template(new TemplateId("System","Test",""));
		assertEquals("System",template.getId().group);
		assertEquals("Test",template.getId().name);
		assertTrue(template.getId().variant.isEmpty());
		assertEquals(Template.STATE_RAW, template.getState());
	}

	@Test
	public void testGetMergableWithoutReplace() throws MergeException {
		template.addDirective(new Replace());
		template.addDirective(new Enrich());
		template.addDirective(new ParseData());
		template.addDirective(new Insert());
		template.cachePrepare(new Cache());

		Cache cache = new Cache();
		Merger merger = new Merger(cache, "system.sample.");
		Template mergable = template.getMergable(merger);

		assertNotSame(template, mergable);
		assertEquals(template.getContent(), mergable.getContent());
		assertEquals(template.getContentEncoding(), mergable.getContentEncoding());
		assertEquals(template.getContentRedirectUrl(), mergable.getContentRedirectUrl());
		assertEquals(template.getContentType(), mergable.getContentType());
		assertEquals(template.getWrapper().front, mergable.getWrapper().front);
		assertEquals(template.getWrapper().back, mergable.getWrapper().back);
		assertEquals(Template.STATE_CACHED, template.getState());
		assertEquals(Template.STATE_MERGABLE, mergable.getState());
		
		assertEquals(template.getDirectives().size(), mergable.getDirectives().size());
		for (int index=0; index < template.getDirectives().size(); index++) {
			assertNotSame(template.getDirectives().get(index), mergable.getDirectives().get(index));
			assertEquals(template.getDirectives().get(index).getClass(), mergable.getDirectives().get(index).getClass());
		}
	}
	
	@Test
	public void testGetMergableWithReplace() throws MergeException {
		HashMap<String, String> replace = new HashMap<String,String>();
		replace.put("foo", "bar");
		replace.put("one", "two");
		template.cachePrepare(new Cache());
		Template mergable = template.getMergable(null, replace);
		for (String key : replace.keySet()) {
			assertTrue(mergable.getReplaceStack().containsKey(key));
			assertEquals(replace.get(key), mergable.getReplaceStack().get(key));
		}
	}

	@Test
	public void testGetMergedOutput() throws MergeException {
		Cache cache = new Cache();
		Merger merger = new Merger(cache, "system.sample.");
		template.setContent("Some Simple Content");
		template.cachePrepare(cache);
		Template mergable = template.getMergable(merger);
		assertEquals("Some Simple Content", mergable.getMergedOutput().getValue());
		assertEquals(Template.STATE_MERGED, mergable.getState());
		assertTrue(mergable.getDirectives().isEmpty());
		assertTrue(mergable.getReplaceStack().isEmpty());
	}

	@Test
	public void testAddReplaceStringString() throws MergeException {
		assertEquals(0,template.getReplaceStack().size());
		template.addReplace("From", "TO");
		assertEquals(0,template.getReplaceStack().size());
		template.cachePrepare(new Cache());
		Template mergable = template.getMergable(null);
		assertEquals(0,mergable.getReplaceStack().size());
		mergable.addReplace("From", "TO");
		assertEquals(1,mergable.getReplaceStack().size());
		assertEquals("TO", mergable.getReplaceStack().get("From"));
	}

	@Test
	public void testAddReplaceHashMap() throws MergeException {
		HashMap<String,String> replace = new HashMap<String,String>();
		replace.put("foo", "bar");
		replace.put("one", "two");
		template.cachePrepare(new Cache());
		template = template.getMergable(null);
		template.addReplace(replace);
		assertEquals(2, template.getReplaceStack().size());
		assertEquals("bar", template.getReplaceStack().get("foo"));
		assertEquals("two", template.getReplaceStack().get("one"));
	}
	
	@Test
	public void testBlankReplace() throws MergeException {
		template.cachePrepare(new Cache());
		template = template.getMergable(null);
		template.addReplace("one", "one");
		template.addReplace("two", "two");
		template.addReplace("three", "three");
		template.addReplace("foo", "bar");
		assertEquals(4, template.getReplaceStack().size());
		HashSet<String> clear = new HashSet<String>();
		clear.add("two");
		clear.add("foo");
		template.blankReplace(clear);
		assertEquals(4, template.getReplaceStack().size());
		assertFalse(template.getReplaceStack().get("one").isEmpty());
		assertTrue(template.getReplaceStack().get("two").isEmpty());
		assertFalse(template.getReplaceStack().get("three").isEmpty());
		assertTrue(template.getReplaceStack().get("foo").isEmpty());
	}

	@Test
	public void testSetGetContent() throws MergeException {
		template.setContent("test");
		assertEquals("test", template.getContent());
	}

	@Test
	public void testClearContent() throws MergeException {
		template.setContent("test");
		assertEquals("test", template.getContent());
		template.clearContent();
		assertEquals("test", template.getContent());
		template.cachePrepare(new Cache());
		assertEquals("test", template.getContent());
		template = template.getMergable(null);
		assertEquals("test", template.getContent());
		template.clearContent();
		assertTrue(template.getContent().isEmpty());
	}

	@Test
	public void testSetGetContentType() {
		template.setContentType("application/html");
		assertEquals("application/html", template.getContentType());
		template.setContentType("text/plain");
		assertEquals("text/plain", template.getContentType());
	}

	@Test
	public void testSetGetContentDisposition() throws MergeException {
		Cache cache = new Cache();
		Merger merger = new Merger(cache, "system.sample.");
		template.cachePrepare(cache);
		Template mergable = template.getMergable(merger);
		assertEquals("attachment;filename=\"FileToReturn.json\"", mergable.getContentDisposition());
	}

	@Test
	public void testSetGetReturn() throws MergeException {
		template.setMergeReturn(Template.RETURN_ARCHIVE);
		assertEquals(Template.RETURN_ARCHIVE, template.getMergeReturn());
		template.setMergeReturn(Template.RETURN_CONTENT);
		assertEquals(Template.RETURN_CONTENT, template.getMergeReturn());
		template.setMergeReturn(Template.RETURN_FORWARD);
		assertEquals(Template.RETURN_FORWARD, template.getMergeReturn());
	}

	@Test
	public void testEncodedContent1() throws Throwable {
		Cache cache = new Cache();
		Template template = new Template("test", "encode", "", "A test {foo} tag");
		template.setContentEncoding(TagSegment.ENCODE_XML);
		Replace replace = new Replace();
		replace.setDataSource("foo");
		replace.setIfPrimitive(Replace.PRIMITIVE_REPLACE);
		replace.setProcessAfter(true);
		template.addDirective(replace);
		cache.postTemplate(template);
		
		Merger context = new Merger(cache, "test.encode.");
		context.getMergeData().put("foo", "-", new DataPrimitive("b&r"));
		template = context.merge();
		assertEquals("A test b&amp;r tag", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testSetGetContentEncoding() throws MergeException {
		for (int index : TagSegment.ENCODE_VALUES().values()) {
			if (index != TagSegment.ENCODE_DEFAULT) {
				template.setContentEncoding(index);
				assertEquals(index, template.getContentEncoding());
			}
		}
		template.setContentEncoding(TagSegment.ENCODE_XML);
		assertEquals(TagSegment.ENCODE_XML, template.getContentEncoding());
		template.setContentEncoding(0);
		assertEquals(TagSegment.ENCODE_XML, template.getContentEncoding());
		template.cachePrepare(new Cache());
		Template mergable = template.getMergable(null);
		assertEquals(TagSegment.ENCODE_XML, template.getContentEncoding());
		mergable.setContentEncoding(TagSegment.ENCODE_HTML);
		assertEquals(TagSegment.ENCODE_XML, template.getContentEncoding());
	}

	@Test
	public void testSetGetDescription() {
		template.setDescription("foo");
		assertEquals("foo",template.getDescription());
	}

	@Test
	public void testSetGetWrapper() {
		template.setWrapper("<", ">");
		assertEquals("<", template.getWrapper().front);
		assertEquals(">", template.getWrapper().back);
		template.setWrapper("{", "}");
		assertEquals("{", template.getWrapper().front);
		assertEquals("}", template.getWrapper().back);
	}

	@Test
	public void testWrapp() throws MergeException {
		template.setWrapper("<", ">");
		assertEquals("<", template.getWrapper().front);
		assertEquals(">", template.getWrapper().back);
		assertEquals("<test>", template.getWrapper().wrapp("test"));
		template.setWrapper("{", "}");
		assertEquals("{", template.getWrapper().front);
		assertEquals("}", template.getWrapper().back);
		assertEquals("{test}", template.getWrapper().wrapp("test"));
		template.cachePrepare(new Cache());
		Template mergable = template.getMergable(null);
		mergable.setWrapper("<", ">");
		assertEquals("{", template.getWrapper().front);
		assertEquals("}", template.getWrapper().back);
		assertEquals("{test}", template.getWrapper().wrapp("test"));
	}

	@Test
	public void testGetId() throws MergeException {
		TemplateId test = new TemplateId("system", "test", "");
		Template newTemplate = new Template("system", "test", "");
		TemplateId real = newTemplate.getId();
		assertEquals(test.group, real.group);
		assertEquals(test.name, real.name);
		assertEquals(test.variant, real.variant);
	}

	@Test
	public void testAddGetDirective() throws MergeException {
		assertEquals(0, template.getDirectives().size());

		template.addDirective(new Enrich());
		assertEquals(1, template.getDirectives().size());
		assertTrue(template.getDirectives().get(0) instanceof Enrich);

		template.addDirective(new Insert());
		assertEquals(2, template.getDirectives().size());
		assertTrue(template.getDirectives().get(1) instanceof Insert);

		template.addDirective(new ParseData());
		assertEquals(3, template.getDirectives().size());
		assertTrue(template.getDirectives().get(2) instanceof ParseData);

		template.addDirective(new Replace());
		assertEquals(4, template.getDirectives().size());
		assertTrue(template.getDirectives().get(3) instanceof Replace);

		template.addDirective(new SaveFile());
		assertEquals(5, template.getDirectives().size());
		assertTrue(template.getDirectives().get(4) instanceof SaveFile);
		
	}

	@Test
	public void testisMerged() throws MergeException {
		Cache cache = new Cache();
		cache.postTemplate(template);
		Merger merger = new Merger(cache, "System.Test.");
		assertEquals(Template.STATE_MERGABLE, merger.getBaseTemplate().getState());
		merger.merge();
		assertEquals(Template.STATE_MERGED, merger.getBaseTemplate().getState());
	}

	@Test
	public void testSetGetContentRedirectUrl() throws MergeException {
		template.setContentRedirectUrl("Foo");
		assertEquals("Foo", template.getContentRedirectUrl());
		template.cachePrepare(new Cache());
		template = template.getMergable(null);
		template.setContentRedirectUrl("Bar");
		assertEquals("Foo", template.getContentRedirectUrl());
	}

	@Test
	public void cachePrepare() throws MergeException {
		template.cachePrepare(new Cache());
		// TODO test primitive validation 
		// TODO test transient population
		
		// Test template stats
		assertEquals("System.Test.", template.getStats().name);
		assertEquals(0, template.getStats().hits);
		assertEquals(12, template.getStats().size);
		assertEquals(0, template.getStats().time);
	}
	
}
