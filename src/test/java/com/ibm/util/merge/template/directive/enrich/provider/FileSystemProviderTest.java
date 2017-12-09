package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.Enrich;

public class FileSystemProviderTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFileSystemProvider() throws MergeException {
		Config config = new Config("{\"envVars\":{\"aFolder.PATH\":\"/opt/ibm/idmu\"}}");
		Cache cache = new Cache(config);
		Template template = new Template("system", "test", "", "Content");
		Enrich directive = new Enrich();
		directive.setEnrichSource("aFolder");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "system.test.");
		template = template.getMergable(context);
		directive = (Enrich) template.getDirectives().get(0);
		FileSystemProvider provider = new FileSystemProvider();
		provider.loadBasePath(directive);
		assertEquals("/opt/ibm/idmu", provider.getBasePath().toString());
	}

	@Test
	public void testProvide() throws MergeException, IOException {
		// Create some files to provide
		File folder = new File("src/test/resources/datafiles");
		assertTrue(folder.exists());
		
		// Test the Provider
		Config config = new Config("{\"envVars\":{\"db.PATH\":\"src/test/resources/datafiles\"}}");
		Cache cache = new Cache(config);
		Template template = new Template("system", "test", "", "Content");
		Enrich directive = new Enrich();
		directive.setEnrichSource("db");
		directive.setEnrichCommand(".*");
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "system.test.");
		template = template.getMergable(context);
		directive = (Enrich) template.getDirectives().get(0);
		FileSystemProvider provider = new FileSystemProvider();
		DataElement result = provider.provide(directive);
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("simple.csv"));
		assertEquals("col1,col2,col3\nr1c1,r1c2,r1c3\nr2c1,r2c2,r2c3\n", result.getAsObject().get("simple.csv").getAsPrimitive());
		
		assertTrue(result.getAsObject().containsKey("simple.txt"));
		assertEquals("Test Text File", result.getAsObject().get("simple.txt").getAsPrimitive());
		
	}

	@Test
	public void testProvideParseAsCsv() throws MergeException, IOException {
		// Create some files to provide
		File folder = new File("src/test/resources/datafiles");
		assertTrue(folder.exists());
		
		// Test the Provider
		Config config = new Config("{\"envVars\":{\"db.PATH\":\"src/test/resources/datafiles\"}}");
		Cache cache = new Cache(config);
		Template template = new Template("system", "test", "", "Content");
		Enrich directive = new Enrich();
		directive.setEnrichSource("db");
		directive.setEnrichCommand("simple.csv");
		directive.setParseAs(Config.PARSE_CSV);
		template.addDirective(directive);
		cache.postTemplate(template);
		Merger context = new Merger(cache, "system.test.");
		template = template.getMergable(context);
		directive = (Enrich) template.getDirectives().get(0);
		FileSystemProvider provider = new FileSystemProvider();
		DataElement result = provider.provide(directive);
		assertTrue(result.isObject());
		assertTrue(result.getAsObject().containsKey("simple.csv"));
		assertTrue(result.getAsObject().get("simple.csv").isList());
		assertEquals("r1c1", result.getAsObject().get("simple.csv").getAsList().get(0).getAsObject().get("col1").getAsPrimitive());
	}

}
