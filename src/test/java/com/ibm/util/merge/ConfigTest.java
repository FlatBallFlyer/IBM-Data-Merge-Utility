package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.*;

import com.google.gson.JsonElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.ProviderInterface;

public class ConfigTest {
	private transient static final DataProxyJson proxy = new DataProxyJson();

	@Before
	public void setUp() throws MergeException {
		Config.initialize();
	}
	
	@Test
	public void testConfigDefault() throws MergeException {
		assertEquals("/opt/ibm/idmu/archives", Config.tempFolder());
		assertEquals("foo", Config.loadFolder());
		assertEquals(2, Config.nestLimit());
	}

	@Test
	public void testConfigString() throws MergeException {
		String configString = 
				"{\"tempFolder\": \"/opt/ibm/idmu/foo\",\"loadFolder\": \"/opt/ibm/idmu/bar\",\"nestLimit\": 99,\"insertLimit\": 88, envVars : {\"test\":\"value\"}}";
		Config.load(configString);
		assertEquals("/opt/ibm/idmu/foo", Config.tempFolder());
		assertEquals("/opt/ibm/idmu/bar", Config.loadFolder());
		assertEquals(99, Config.nestLimit());
		assertEquals(88, Config.insertLimit());
		assertEquals("value", Config.env("test"));
	}

	@Test
	public void testConfigOptions() throws MergeException {
		String optString = Config.allOptions();
		JsonElement options = proxy.fromString(optString, JsonElement.class);
		assertTrue(options.isJsonArray());
		assertEquals(2, options.getAsJsonArray().size());
		options = options.getAsJsonArray().get(0);
		assertTrue(options.getAsJsonObject().has("Template"));
		assertTrue(options.getAsJsonObject().has("Template"));
		assertTrue(options.getAsJsonObject().has("Encoding"));
//		assertTrue(options.getAsJsonObject().has("Parser"));
		assertTrue(options.getAsJsonObject().has("Enrich"));
		assertTrue(options.getAsJsonObject().has("Insert"));
		assertTrue(options.getAsJsonObject().has("Parse"));
		assertTrue(options.getAsJsonObject().has("Replace"));
		assertTrue(options.getAsJsonObject().has("Save"));
	}

	@Test
	public void testDefaultProviders() throws MergeException {
		HashMap<String, Class<ProviderInterface>> providers = Config.providers(); 
		assertTrue(providers.containsKey("com.ibm.util.merge.template.directive.enrich.provider.CacheProvider"));
		assertTrue(providers.containsKey("com.ibm.util.merge.template.directive.enrich.provider.CloudantProvider"));
		assertTrue(providers.containsKey("com.ibm.util.merge.template.directive.enrich.provider.FileSystemProvider"));
		assertTrue(providers.containsKey("com.ibm.util.merge.template.directive.enrich.provider.JdbcProvider"));
		assertTrue(providers.containsKey("com.ibm.util.merge.template.directive.enrich.provider.JndiProvider"));
		assertTrue(providers.containsKey("com.ibm.util.merge.template.directive.enrich.provider.MongoProvider"));
		assertTrue(providers.containsKey("com.ibm.util.merge.template.directive.enrich.provider.RestProvider"));
		assertTrue(providers.containsKey("com.ibm.util.merge.template.directive.enrich.provider.StubProvider"));
	}
	
	@Test
	public void testCustomProviders() throws MergeException {
		String configString = "{ \"tempFolder\" : \"/opt/ibm/idmu/archives\", " +
			" \"loadFolder\" : \"src/test/resources/functional/fileProviderTest\"," +
			" \"logLevel\" : \"SEVERE\"," +
			" \"nestLimit\" : 3," +
			" \"insertLimit\" : 20," +
			" \"defaultProviders\" : [" +
			"	\"com.ibm.util.merge.template.directive.enrich.provider.FileSystemProvider\"" +  
			" ]," +
			" \"envVars\" : {" +
			" \"test\" : \"value\"" +
			" }" +
			"}";
		Config.load(configString);
		assertEquals(1, Config.providers().size());
		assertTrue(Config.providers().containsKey("com.ibm.util.merge.template.directive.enrich.provider.FileSystemProvider"));
	}
	
	@Test
	public void testGetSetTempFolder() throws MergeException {
		Config config = new Config();
		config.setTempFolder("Foo");
		assertEquals("Foo", config.getTempFolder());
	}

	@Test
	public void testGetSetNestLimit() throws MergeException {
		Config config = new Config();
		assertEquals(2, Config.nestLimit());
		config.setNestLimit(44);
		assertEquals(44, config.getNestLimit());
	}

	@Test
	public void testGetSetInsertLimit() throws MergeException {
		Config config = new Config();
		assertEquals(20, config.getInsertLimit());
		config.setInsertLimit(44);
		assertEquals(44, config.getInsertLimit());
	}

	@Test
	public void testGetEnv1() throws MergeException {
		Config config = new Config();
		config.getEnvVars().put("Test", "Foo");
		assertEquals("Foo", config.getEnv("Test"));
	}

	@Test
	public void testGetEnv2() throws MergeException {
		Config config = new Config();
		config.getEnvVars().put("VCAP_SERVICES", "{\"SERVICE_NAME\":[\"Some Service JSON\"]}");
		assertEquals("\"Some Service JSON\"", config.getEnv("VCAP:SERVICE_NAME"));
	}

	@Test
	public void testGetEnv4() throws MergeException {
		Config config = new Config();
		config.getEnvVars().put("VCAP_SERVICES", "{\"SERVICE_NAME\":[\"Some Service JSON\"]}");
		try {
			Config.env("VCAP:FOO");
		} catch (MergeException e) {
			// success
			return;
		}
		fail("Merge Exception expected!");
	}

	@Test
	public void testGetEnv5() throws MergeException {
		Config config = new Config();
		config.getEnvVars().put("VCAP_SERVICES", "{\"SERVICE_NAME\":\"Some Service JSON\"}");
		try {
			Config.env("VCAP:SERVICE_NAME");
		} catch (MergeException e) {
			// success
			return;
		}
		fail("Merge Exception expected!");
	}

}
