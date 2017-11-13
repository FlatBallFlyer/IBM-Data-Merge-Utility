package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public class FileSystemProviderTest {
	private TemplateCache cache;
	private Template template;
	private Merger context;
	
	@Before
	public void setUp() throws Exception {
		Config.initialize();
		cache = new TemplateCache();
		template = new Template("system", "test", "", "Content");
		cache.postTemplate(template);
		context = new Merger(cache, "system.test.");
	}

	@Test
	public void testFileSystemProvider() throws MergeException {
		FileSystemProvider provider = new FileSystemProvider("aFolder", "/opt/ibm/idmu", context);
		assertEquals("aFolder", provider.getSource());
		assertEquals("/opt/ibm/idmu", provider.getDbName());
		assertSame(context, provider.getContext());
	}

	@Test
	public void testProvide() throws MergeException, IOException {
		// Create some files to provide
		File folder = new File("src/test/resources/http");
		assertTrue(folder.exists());
		
		// Test the Provider
		FileSystemProvider provider = new FileSystemProvider("db", "src/test/resources/http", context);
		DataElement result = provider.provide(".*", template.getWrapper(), context, template.getReplaceStack(), Parser.PARSE_NONE);
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("simple.csv"));
		assertEquals("col1,col2,col3\nr1c1,r1c2,r1c3\nr2c1,r2c2,r2c3\n", result.getAsObject().get("simple.csv").getAsPrimitive());
		
		assertTrue(result.getAsObject().containsKey("simple.txt"));
		assertEquals("Test Text File", result.getAsObject().get("simple.txt").getAsPrimitive());
		
	}

	@Test
	public void testProvideParseAsCsv() throws MergeException, IOException {
		// Create some files to provide
		File folder = new File("src/test/resources/http");
		assertTrue(folder.exists());
		
		// Test the Provider
		FileSystemProvider provider = new FileSystemProvider("db", "src/test/resources/http", context);
		DataElement result = provider.provide("simple.csv", template.getWrapper(), context, template.getReplaceStack(), Parser.PARSE_CSV);
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("simple.csv"));
		assertTrue(result.getAsObject().get("simple.csv").isList());
		assertEquals("r1c1", result.getAsObject().get("simple.csv").getAsList().get(0).getAsObject().get("col1").getAsPrimitive());
	}

}
