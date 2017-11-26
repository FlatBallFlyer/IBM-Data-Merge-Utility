package com.ibm.util.merge;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.gson.JsonElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;

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
		String optString = Config.get();
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
		assertTrue(Config.hasProvider("com.ibm.util.merge.template.directive.enrich.provider.FileSystemProvider"));
	}
	
	@Test
	public void testGetTempFolder() throws MergeException {
		assertEquals("/opt/ibm/idmu/archives", Config.tempFolder());
		String configString = 
				"{\"tempFolder\": \"Bar\"}";
		Config.load(configString);
		assertEquals("Bar", Config.tempFolder());
	}

	@Test
	public void testGetNestLimit() throws MergeException {
		assertEquals(2, Config.nestLimit());
		Config.load("{\"nestLimit\": \"44\"}");
		assertEquals(44, Config.nestLimit());
	}

	@Test
	public void testGetSetInsertLimit() throws MergeException {
		assertEquals(20, Config.insertLimit());
		Config.load("{\"nestLimit\": 44}");
		assertEquals(44, Config.nestLimit());
	}

	@Test
	public void testGetEnv1() throws MergeException {
		Config.load("{\"envVars\":{\"Test\":[\"Foo\"]}}");
		assertEquals("Foo", Config.env("Test"));
	}

	@Test
	public void testGetEnv2() throws MergeException {
		Config.load("{\"envVars\":{\"VCAP_SERVICES\":\"{\\\"SERVICE_NAME\\\":[\\\"Some Service JSON\\\"]}\"}}");
		assertEquals("\"Some Service JSON\"", Config.env("VCAP:SERVICE_NAME"));
	}

	@Test
	public void testGetEnv4() throws MergeException {
		Config.load("{\"envVars\":{\"VCAP_SERVICES\":\"{\\\"SERVICE_NAME\\\":[\\\"Some Service JSON\\\"]}\"}}");
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
		Config.load("{\"envVars\":{\"VCAP_SERVICES\":\"{\\\"SERVCE_NAME\\\":[\\\"Some Service JSON\\\"]}\"}}");
		try {
			Config.env("VCAP:SERVICE_NAME");
		} catch (MergeException e) {
			// success
			return;
		}
		fail("Merge Exception expected!");
	}

}
