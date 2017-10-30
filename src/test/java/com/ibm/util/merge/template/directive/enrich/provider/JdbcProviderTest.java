package com.ibm.util.merge.template.directive.enrich.provider;
/*
 *   "idmu-jdbc1": [
        {
            "credentials": {
                "db_type": "sql",
                "name": "testsql",
                "uri_cli": "testcli",
                "ca_certificate_base64": "test64",
                "deployment_id": "testid",
                "uri": "testuri"
            }]
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
import com.ibm.util.merge.template.directive.enrich.provider.JdbcProvider;
import com.ibm.util.merge.template.directive.enrich.source.JdbcSource;

public class JdbcProviderTest {
	DataProxyJson proxy = new DataProxyJson();
	Config config;
	TemplateCache cache;
	Merger context;
	Template template;
	JdbcSource source;
	JdbcProvider provider;
	
	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		context = new Merger(cache, config, "system.sample.");

		template = new Template("test","provider","rest","File System Provider Test");
		template.setWrapper("{", "}");

		source = new JdbcSource();
		source.setGetCommand("select * from templates");
		source.setPutCommand("update templates set id = \"{id}\", template = \"{template}\" where id = \"{id}\"");
		source.setPostCommand("insert into templates (id, template) values {id}, {template}");
		source.setDeleteCommand("delete * from templates where id = \"{id}\"");
		provider = new JdbcProvider(source);
	}

	@Test
	public void testJdbcProvider() {
		assertEquals(AbstractProvider.PROVIDER_JDBC, provider.getType());
	}

	@Test
	public void testGetPostPutDelete() throws MergeException {
		fail("Requires JNDI Data Source");
		provider = (JdbcProvider) source.getProvider();
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
		fail("Requires JNDI Data Source");
		provider = (JdbcProvider) source.getProvider();
		
		DataElement reply = provider.get(template);
		assertTrue(reply.isList());
		assertEquals(0, reply.getAsList().size());
	}

	@Test
	public void testPutMissing() throws MergeException {
		fail("Requires JNDI Data Source");
		provider = (JdbcProvider) source.getProvider();
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
		fail("Requires JNDI Data Source");
		provider = (JdbcProvider) source.getProvider();
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
		fail("Requires JNDI Data Source");
		provider = (JdbcProvider) source.getProvider();
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

