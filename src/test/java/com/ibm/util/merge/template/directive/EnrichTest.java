package com.ibm.util.merge.template.directive;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public class EnrichTest {
	DataProxyJson gsonProxy;	
	TemplateCache cache;
	Merger context;
	Template template;
	Enrich enrich;
	
	@Before
	public void setUp() throws Exception {
		gsonProxy = new DataProxyJson(false);
		Config.initialize();
		cache = new TemplateCache();
		template = new Template("test", "enrich", "", "Template Content", "{", "}");
		enrich = new Enrich();
		enrich.setTargetDataName("test");
		enrich.setName("test");
		template.addDirective(enrich);
//		cache.postTemplate(template);
//		context = new Merger(cache, config, "test.enrich.");
//		context.merge();
	}

	@Test
	public void testEnrich() throws MergeException {
		Enrich enrich = new Enrich();
		assertEquals(AbstractDirective.TYPE_ENRICH, enrich.getType());
		assertEquals("",							enrich.getName());
		assertEquals("stub", 						enrich.getTargetDataName());
		assertEquals("-", 							enrich.getTargetDataDelimeter());
		assertEquals("com.ibm.util.merge.template.directive.enrich.provider.StubProvider", 							enrich.getEnrichClass());
		assertEquals("", 							enrich.getEnrichSource());
		assertEquals("", 							enrich.getEnrichParameter());
		assertEquals("", 							enrich.getEnrichCommand());
		assertEquals(Config.PARSE_NONE, 			enrich.getParseAs());
	}

	@Test
	public void testGetMergable() throws MergeException {
		Enrich before = new Enrich();
		before.setEnrichClass("com.ibm.util.merge.template.directive.enrich.provider.StubProvider");
		before.setEnrichCommand("foo");
		before.setEnrichParameter("somedb");
		before.setEnrichSource("some source");
		before.setName("Foo");
		before.setParseAs(Config.PARSE_JSON);
		before.setTargetDataName("some=name");
		before.setTargetDataDelimeter("=");
		before.cachePrepare(template);
		Enrich after = (Enrich) before.getMergable();
		assertNotSame(before, after);
		assertEquals(before.getEnrichClass(), after.getEnrichClass());
		assertEquals(before.getEnrichCommand(), after.getEnrichCommand());
		assertEquals(before.getEnrichParameter(), after.getEnrichParameter());
		assertEquals(before.getEnrichSource(), after.getEnrichSource());
		assertEquals(before.getName(), after.getName());
		assertEquals(before.getParseAs(), after.getParseAs());
		assertEquals(before.getTargetDataName(), after.getTargetDataName());
		assertEquals(before.getTargetDataDelimeter(), after.getTargetDataDelimeter());
	}
	
	@Test 
	public void testExecuteNoOp() throws MergeException {
		TemplateCache cache = new TemplateCache();
		Template template = new Template("test","noop","", "Simple Test");
		Enrich directive = new Enrich();
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger merger = new Merger(cache, "test.noop.");
		template = merger.merge();
		assertEquals("Simple Test", template.getMergedOutput().getValue());
	}
	
	@Test
	public void testExecuteNoParse() throws MergeException {
		cache.postTemplate(template);
		context = new Merger(cache, "test.enrich.");
		context.merge();
		Template test = gsonProxy.fromString(context.getMergeData().get("test", "\"").getAsPrimitive(), Template.class);
		assertTrue(test instanceof Template);
	}

	@Test
	public void testExecuteParse() throws MergeException {
		enrich.setParseAs(Config.PARSE_JSON);
		cache.postTemplate(template);
		context = new Merger(cache, "test.enrich.");
		context.merge();
		DataElement output = context.getMergeData().get("test", "\"");
		assertTrue(output.isObject());
		assertTrue(output.getAsObject().containsKey("id"));
		assertTrue(output.getAsObject().containsKey("content"));
		assertTrue(output.getAsObject().containsKey("wrapper"));
	}

	@Test
	public void testTargetTags() throws MergeException {
		TemplateCache cache = new TemplateCache();
		Template template = new Template("test", "targetTags", "", "Some Content", "{", "}");
		Enrich directive = new Enrich();
		directive.setTargetDataName("some{foo}name");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "test.targetTags.");
		context.getBaseTemplate().addReplace("foo", "bar");
		template = context.merge();
		assertTrue(context.getMergeData().contians("somebarname", "-"));
	}
	
	@Test
	public void testGetSetTargetDataName() {
		enrich.setTargetDataName("foo");
		assertEquals("foo", enrich.getTargetDataName());
	}

	@Test
	public void testGetSetEnrichSource() {
		enrich.setEnrichSource("foo");
		assertEquals("foo", enrich.getEnrichSource());
	}

	@Test
	public void testGetSetTargetDataDelimeter() {
		enrich.setTargetDataDelimeter("foo");
		assertEquals("foo", enrich.getTargetDataDelimeter());
	}

	@Test
	public void testGetSetParseAs() {
		enrich.setParseAs(Config.PARSE_CSV);
		assertEquals(Config.PARSE_CSV, enrich.getParseAs());
	}

	@Test
	public void testGetSetEnrichClass() {
		enrich.setEnrichClass("foo");
		assertEquals("foo", enrich.getEnrichClass());
	}

	@Test
	public void testGetSetEnrichCommand() throws MergeException {
		enrich.setEnrichCommand("foo");
		assertEquals("foo", enrich.getEnrichCommand());
	}

	@Test
	public void testGetSetEnrichParameter() {
		enrich.setEnrichParameter("foo");
		assertEquals("foo", enrich.getEnrichParameter());
	}

}
