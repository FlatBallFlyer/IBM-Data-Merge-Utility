package com.ibm.util.merge.template.directive.enrich.provider;
/*
 * SPECIAL TESTING REQUIREMENTS
 * Environment Variable VCAP_SERVICES with at least {"idmu-mongo1": [{"credentials": {"username":"user","password":"password","host":"localhost","port":"80","url":"connectstring"}}]}
 */


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
import com.ibm.util.merge.template.directive.enrich.provider.AbstractProvider;
import com.ibm.util.merge.template.directive.enrich.provider.MongoProvider;
import com.ibm.util.merge.template.directive.enrich.source.MongoSource;

public class MongoProviderTest {
	DataProxyJson proxy = new DataProxyJson();
	Config config;
	TemplateCache cache;
	Merger context;
	Template template;
	MongoSource source;
	MongoProvider provider;

	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		context = new Merger(cache, config, "system.sample.");

		template = new Template("test","provider","rest","File System Provider Test");
		template.setWrapper("{", "}");

		source = new MongoSource();
		source.setGetCommand("mongo select structure");
		source.setPutCommand("mongo update structure");
		source.setPostCommand("mongo insert structure");
		source.setDeleteCommand("mongo delete structure");
		provider = (MongoProvider) source.getProvider();
	}

	@Test
	public void testMongoProvider() {
		assertEquals(AbstractProvider.PROVIDER_MONGO, provider.getType());
	}

	@Test
	public void testGetPostPutDelete() throws MergeException {
		fail("Requires Mongo Data Source");
		provider = (MongoProvider) source.getProvider();
		provider.post(template);
		
		template = template.getMergable(context);
		DataElement reply = provider.get(template);
		template = proxy.fromJSON(reply.getAsList().get(0).getAsPrimitive(), Template.class);
		assertEquals("File System Provider Test", template.getContent());
		
		template.setContent("Some New Content");
		provider.put(template);
		
		template = template.getMergable(context);
		reply = provider.get(template);
		template = proxy.fromJSON(reply.getAsList().get(0).getAsPrimitive(), Template.class);
		assertEquals("Some New Content", template.getContent());

		provider.delete(template);
	}

	@Test
	public void testGetMissing() throws MergeException {
		fail("Requires Mongo Data Source");
		provider = (MongoProvider) source.getProvider();
		
		DataElement reply = provider.get(template);
		assertTrue(reply.isList());
		assertEquals(0, reply.getAsList().size());
	}

	@Test
	public void testPutMissing() throws MergeException {
		fail("Requires Mongo Data Source");
		provider = (MongoProvider) source.getProvider();
		template = template.getMergable(context);
		
		try {
			provider.put(template);
		} catch (MergeException e) {
			// expected
			return;
		}
		fail("Missing Put failed to throw exception");
	}

	@Test
	public void testDeleteMissing() throws MergeException {
		fail("Requires Mongo Data Source");
		provider = (MongoProvider) source.getProvider();
		template = template.getMergable(context);
		
		try {
			provider.delete(template);
		} catch (MergeException e) {
			// expected
			return;
		}
		fail("Missing Delete failed to throw exception");
	}

	@Test
	public void testPostDuplicate() throws MergeException {
		fail("Requires Mongo Data Source");
		provider = (MongoProvider) source.getProvider();
		provider.post(template);

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
