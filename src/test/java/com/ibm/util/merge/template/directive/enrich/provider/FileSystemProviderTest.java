package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;

public class FileSystemProviderTest {
	private Config config;
	private TemplateCache cache;
	private Template template;
	private Merger context;
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		template = new Template("system", "test", "", "Content");
		cache.postTemplate(template);
		context = new Merger(cache, config, "system.test.");
	}

	@Test
	public void testFileSystemProvider() throws MergeException {
		FileSystemProvider provider = new FileSystemProvider("source", "db", context);
		assertEquals("source", provider.getSource());
		assertEquals("db", provider.getDbName());
		assertSame(context, provider.getContext());
	}

	@Test
	public void testProvide() throws MergeException {
		// Create some files to provide
		
		// Test the Provider
		FileSystemProvider provider = new FileSystemProvider("source", "db", context);
		DataElement result = provider.provide("*", template.getWrapper(), context, template.getReplaceStack());
		assertTrue(result.isList());
		
		// Clean up files
		
		fail("Not yet implemented");
	}


}
