package com.ibm.util.merge.template;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.*;
import com.ibm.util.merge.exception.*;

public class TemplateTest {
	private Template template;
	
	@Before
	public void setUp() throws Exception {
		Config.initialize();
		template = new Template("System","Test","");
		template.setContent("Test Content");
		template.setDescription("A testing template");
		template.setContentDisposition(Template.DISPOSITION_DOWNLOAD);
		template.setContentEncoding(TagSegment.ENCODE_JSON);
		template.setContentFileName("FileToReturn.json");
		template.setContentType(Template.CONTENT_JSON);
		template.setContentRedirectUrl("Redirect");
		template.setWrapper("<", ">");
	}

	@Test
	public void testTemplate() throws MergeException {
		template = new Template();
		assertEquals("void",template.getId().group);
		assertEquals("void",template.getId().name);
		assertEquals("void",template.getId().variant);
		assertFalse(template.isMergable());
		assertFalse(template.isMerged());
	}

	@Test
	public void testTemplateStringStringString() throws MergeException {
		template = new Template("System","Test","");
		assertEquals("System",template.getId().group);
		assertEquals("Test",template.getId().name);
		assertTrue(template.getId().variant.isEmpty());
		assertFalse(template.isMergable());
		assertFalse(template.isMerged());
	}

	@Test
	public void testTemplateStringStringStringString() throws MergeException {
		template = new Template("System","Test", "", "SomeContent");
		assertEquals("System",template.getId().group);
		assertEquals("Test",template.getId().name);
		assertTrue(template.getId().variant.isEmpty());
		assertEquals("SomeContent", template.getContent());
		assertFalse(template.isMergable());
		assertFalse(template.isMerged());
	}

	@Test
	public void testTemplateTemplateId() throws MergeException {
		template = new Template(new TemplateId("System","Test",""));
		assertEquals("System",template.getId().group);
		assertEquals("Test",template.getId().name);
		assertTrue(template.getId().variant.isEmpty());
		assertFalse(template.isMergable());
		assertFalse(template.isMerged());
	}

	@Test
	public void testGetMergableWithoutReplace() throws MergeException {
		template.addDirective(new Replace().getMergable());
		template.addDirective(new Enrich().getMergable());
		template.addDirective(new ParseData().getMergable());
		template.addDirective(new Insert().getMergable());

		Template mergable = template.getMergable(null);

		assertNotSame(template, mergable);
		assertEquals(template.getContent(), mergable.getContent());
		assertEquals(template.getContentEncoding(), mergable.getContentEncoding());
		assertEquals(template.getContentFileName(), mergable.getContentFileName());
		assertEquals(template.getContentRedirectUrl(), mergable.getContentRedirectUrl());
		assertEquals(template.getContentType(), mergable.getContentType());
		assertEquals(template.getWrapper().front, mergable.getWrapper().front);
		assertEquals(template.getWrapper().back, mergable.getWrapper().back);
		assertTrue(mergable.isMergable());
		assertFalse(mergable.isMerged());
		
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

		Template mergable = template.getMergable(null, replace);
		for (String key : replace.keySet()) {
			assertTrue(mergable.getReplaceStack().containsKey(key));
			assertEquals(replace.get(key), mergable.getReplaceStack().get(key));
		}
	}

	@Test
	public void testGetMergedOutput() throws MergeException {
		TemplateCache cache = new TemplateCache();
		Merger merger = new Merger(cache, "system.sample.");
		template.setContent("Some Simple Content");
		
		Template mergable = template.getMergable(merger);
		assertEquals("Some Simple Content", mergable.getMergedOutput().getValue());
		assertTrue(mergable.isMerged());
		assertTrue(mergable.getDirectives().isEmpty());
		assertTrue(mergable.getReplaceStack().isEmpty());
	}

	@Test
	public void testAddReplaceStringString() throws MergeException {
		assertEquals(0,template.getReplaceStack().size());
		template.addReplace("From", "TO");
		assertEquals(0,template.getReplaceStack().size());
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
		template = template.getMergable(null);
		template.addReplace(replace);
		assertEquals(2, template.getReplaceStack().size());
		assertEquals("bar", template.getReplaceStack().get("foo"));
		assertEquals("two", template.getReplaceStack().get("one"));
	}
	
	@Test
	public void testBlankReplace() throws MergeException {
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
		template = template.getMergable(null);
		assertEquals("test", template.getContent());
		template.clearContent();
		assertTrue(template.getContent().isEmpty());
	}

	@Test
	public void testSetGetContentType() {
		template.setContentType(Template.CONTENT_HTML);
		assertEquals(Template.CONTENT_HTML, template.getContentType());
		template.setContentType(Template.CONTENT_JSON);
		assertEquals(Template.CONTENT_JSON, template.getContentType());
		template.setContentType(Template.CONTENT_TEXT);
		assertEquals(Template.CONTENT_TEXT, template.getContentType());
		template.setContentType(Template.CONTENT_XML);
		assertEquals(Template.CONTENT_XML, template.getContentType());
		template.setContentType(0);
		assertEquals(Template.CONTENT_XML, template.getContentType());
	}

	@Test
	public void testSetGetContentDisposition() throws MergeException {
		TemplateCache cache = new TemplateCache();
		Merger merger = new Merger(cache, "system.sample.");
		template.setContentDisposition(Template.DISPOSITION_DOWNLOAD);
		template.setContentFileName("Foo");
		Template mergable = template.getMergable(merger);
		assertEquals("attachment;filename=\"Foo\"", mergable.getContentDisposition());
		template.setContentDisposition(Template.DISPOSITION_NORMAL);
		mergable = template.getMergable(null);
		assertEquals("", mergable.getContentDisposition());
	}

	@Test
	public void testSetGetContentFile() {
		template.setContentFileName("FOO");
		assertEquals("FOO", template.getContentFileName());
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
		assertFalse(template.isMerged());
		Template mergable = template.getMergable(null);
		assertFalse(template.isMerged());
		mergable.getMergedOutput();
		assertTrue(mergable.isMerged());
	}

	@Test
	public void testSetGetContentRedirectUrl() throws MergeException {
		template.setContentRedirectUrl("Foo");
		assertEquals("Foo", template.getContentRedirectUrl());
		template = template.getMergable(null);
		template.setContentRedirectUrl("Bar");
		assertEquals("Foo", template.getContentRedirectUrl());
	}

	@Test
	public void cachePrepare() throws MergeException {
		template.cachePrepare();
		// TODO test primitive validation 
		// TODO test transient population
		
		// Test template stats
		assertEquals("System.Test.", template.getStats().name);
		assertEquals(0, template.getStats().hits);
		assertEquals(12, template.getStats().size);
		assertEquals(0, template.getStats().time);
	}
	
	@Test
	public void testGetSetContext() throws MergeException {
		assertEquals(null, template.getContext());
		TemplateCache cache = new TemplateCache();
		Merger context = new Merger(cache, "system.sample.");
		template.setContext(context);
		assertSame(context, template.getContext());
	}
	
}
