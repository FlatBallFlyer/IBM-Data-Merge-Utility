package com.ibm.util.merge;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.gson.JsonElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;

public class ConfigTest {
	private DataProxyJson proxy;

	@Before
	public void setUp() throws MergeException {
		proxy = new DataProxyJson(false);
	}
	
	@Test
	public void testConfigDefault() throws MergeException {
		Config config = new Config();
		assertEquals("/opt/ibm/idmu/archives", config.getTempFolder());
		assertEquals(2, config.getNestLimit());
	}

	@Test
	public void testConfigString() throws MergeException {
		String configString = 
				"{\"tempFolder\": \"/opt/ibm/idmu/foo\",\"nestLimit\": 99,\"insertLimit\": 88, envVars : {\"test\":\"value\"}}";
		Config config = new Config(configString);
		assertEquals("/opt/ibm/idmu/foo", config.getTempFolder());
		assertEquals(99, config.getNestLimit());
		assertEquals(88, config.getInsertLimit());
		assertEquals("value", config.getEnv("test"));
	}

	@Test
	public void testConfigOptions() throws MergeException {
		Config config = new Config();
		String optString = config.get();
		JsonElement options = proxy.fromString(optString, JsonElement.class);
		assertTrue(options.isJsonObject());
		assertTrue(options.getAsJsonObject().has("config"));
		assertTrue(options.getAsJsonObject().has("providers"));
		assertTrue(options.getAsJsonObject().has("parsers"));
		assertTrue(options.getAsJsonObject().has("template"));
		assertTrue(options.getAsJsonObject().has("encoding"));
		assertTrue(options.getAsJsonObject().has("enrich"));
		assertTrue(options.getAsJsonObject().has("insert"));
		assertTrue(options.getAsJsonObject().has("parse"));
		assertTrue(options.getAsJsonObject().has("replace"));
		assertTrue(options.getAsJsonObject().has("save"));
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
		Config config = new Config(configString);
		assertTrue(config.hasProvider("com.ibm.util.merge.template.directive.enrich.provider.FileSystemProvider"));
	}
	
	@Test
	public void testGetTempFolder() throws MergeException {
		Config config = new Config();
		assertEquals("/opt/ibm/idmu/archives", config.getTempFolder());
		String configString = 
				"{\"tempFolder\": \"Bar\"}";
		config = new Config(configString);
		assertEquals("Bar", config.getTempFolder());
	}

	@Test
	public void testGetNestLimit() throws MergeException {
		Config config = new Config("{\"nestLimit\": \"44\"}");
		assertEquals(44, config.getNestLimit());
	}

	@Test
	public void testGetIsPretty() throws MergeException {
		Config config = new Config("{\"prettyJson\": false }");
		assertFalse(config.isPrettyJson());
	}

	@Test
	public void testGetSetInsertLimit() throws MergeException {
		Config config = new Config("{\"nestLimit\": 44}");
		assertEquals(44, config.getNestLimit());
	}

	@Test
	public void testGetEnv1() throws MergeException {
		Config config = new Config("{\"envVars\":{\"Test\":[\"Foo\"]}}");
		assertEquals("Foo", config.getEnv("Test"));
	}

	@Test
	public void testGetEnv2() throws MergeException {
		Config config = new Config("{\"envVars\":{\"VCAP_SERVICES\":\"{\\\"SERVICE_NAME\\\":[\\\"Some Service JSON\\\"]}\"}}");
		assertEquals("\"Some Service JSON\"", config.getEnv("VCAP:SERVICE_NAME"));
	}

	@Test
	public void testGetEnv4() throws MergeException {
		Config config = new Config("{\"envVars\":{\"VCAP_SERVICES\":\"{\\\"SERVICE_NAME\\\":[\\\"Some Service JSON\\\"]}\"}}");
		try {
			config.getEnv("VCAP:FOO");
		} catch (MergeException e) {
			// success
			return;
		}
		fail("Merge Exception expected!");
	}

	@Test
	public void testGetEnv5() throws MergeException {
		Config config = new Config("{\"envVars\":{\"VCAP_SERVICES\":\"{\\\"SERVCE_NAME\\\":[\\\"Some Service JSON\\\"]}\"}}");
		try {
			config.getEnv("VCAP:SERVICE_NAME");
		} catch (MergeException e) {
			// success
			return;
		}
		fail("Merge Exception expected!");
	}

}
