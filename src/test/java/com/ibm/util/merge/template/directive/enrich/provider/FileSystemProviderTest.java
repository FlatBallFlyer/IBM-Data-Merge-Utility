package com.ibm.util.merge.template.directive.enrich.provider;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.source.FileSystemSource;

public class FileSystemProviderTest {
	DataProxyJson proxy = new DataProxyJson();
	Config config;
	TemplateCache cache;
	Merger context;
	Template template;
	FileSystemSource source;
	FileSystemProvider provider;
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		context = new Merger(cache, config, "system.sample.");

		template = new Template("test","provider","rest","File System Provider Test");
		template.setWrapper("{", "}");
		source = new FileSystemSource();
		source.setEnv("VCAP:idmu-rest1");
		source.setName("idmu-test");
		source.setFileSystemPath("src/test/resources/http");
		source.setGetCommand("{path}");
	}

	@Test
	public void testFileSystemProvider() throws MergeException {
		provider = (FileSystemProvider) source.getProvider();
		assertTrue(provider instanceof FileSystemProvider);
	}
	
	@Test
	public void testGet() throws MergeException {
		template = template.getMergable(context);
		template.addReplace("path", "simple.txt");
		provider = (FileSystemProvider) source.getProvider();
		DataElement response = provider.get(template);
		assertTrue(response.isList());
		assertTrue(response.getAsList().get(0).isPrimitive());
		assertEquals("Test Text File", response.getAsList().get(0).getAsPrimitive());
	}

	@Test
	public void testPostPutDeleteSucccess() throws MergeException {
		provider = (FileSystemProvider) source.getProvider();
		provider.post(template);
		assertTrue(new File("src/test/resources/http/test.provider.rest.json").exists());
		
		template = template.getMergable(context);
		template.addReplace("path", "test\\.provider\\.rest\\.json");
		DataElement reply = provider.get(template);
		template = proxy.fromJSON(reply.getAsList().get(0).getAsPrimitive(), Template.class);
		assertEquals("File System Provider Test", template.getContent());
		
		template.setContent("Some New Content");
		provider.put(template);
		
		template = template.getMergable(context);
		template.addReplace("path", "test\\.provider\\.rest\\.json");
		reply = provider.get(template);
		template = proxy.fromJSON(reply.getAsList().get(0).getAsPrimitive(), Template.class);
		assertEquals("Some New Content", template.getContent());

		provider.delete(template);
		assertFalse(new File("src/test/resources/http/test.provider.rest.json").exists());
	}

	@Test
	public void testGetMissing() throws MergeException {
		provider = (FileSystemProvider) source.getProvider();
		template = template.getMergable(context);
		template.addReplace("path", "test\\.provider\\.rest\\.json");
		assertFalse(new File("src/test/resources/http/test.provider.rest.json").exists());
		
		DataElement reply = provider.get(template);
		assertTrue(reply.isList());
		assertEquals(0, reply.getAsList().size());
	}

	@Test
	public void testPutMissing() throws MergeException {
		provider = (FileSystemProvider) source.getProvider();
		template = template.getMergable(context);
		template.addReplace("path", "test\\.provider\\.rest\\.json");
		assertFalse(new File("src/test/resources/http/test.provider.rest.json").exists());
		
		try {
			provider.put(template);
		} catch (MergeException e) {
			return;
		}
		fail("Missing Put failed to throw exception");
	}

	@Test
	public void testDeleteMissing() throws MergeException {
		provider = (FileSystemProvider) source.getProvider();
		template = template.getMergable(context);
		template.addReplace("path", "test\\.provider\\.rest\\.json");
		assertFalse(new File("src/test/resources/http/test.provider.rest.json").exists());
		
		try {
			provider.delete(template);
		} catch (MergeException e) {
			return;
		}
		fail("Missing Delete failed to throw exception");
	}

	@Test
	public void testPostDuplicate() throws MergeException {
		provider = (FileSystemProvider) source.getProvider();
		provider.post(template);
		assertTrue(new File("src/test/resources/http/test.provider.rest.json").exists());

		try {
			provider.post(template);
		} catch (MergeException e) {
			// expected
			provider.delete(template);
			assertFalse(new File("src/test/resources/http/test.provider.rest.json").exists());
			return;
		}
		fail("Duplicate Post failed to throw exception");
	}

}
