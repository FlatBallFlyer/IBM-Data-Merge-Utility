package com.ibm.util.merge;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.gson.JsonElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.MergeException;

public class ConfigTest {
	private transient static final DataProxyJson proxy = new DataProxyJson();

	@Test
	public void testConfigDefault() throws MergeException {
		Config config = new Config();
		assertEquals("/opt/ibm/idmu/archives", config.getTempFolder());
		assertEquals("foo", config.getLoadFolder());
		assertEquals(2, config.getNestLimit());
	}

	@Test
	public void testConfigString() throws MergeException {
		String configString = 
				"{\"tempFolder\": \"/opt/ibm/idmu/foo\",\"loadFolder\": \"/opt/ibm/idmu/bar\",\"nestLimit\": 99,\"insertLimit\": 88, envVars : {\"test\":\"value\"}}";
		Config config = new Config(configString);
		assertEquals("/opt/ibm/idmu/foo", config.getTempFolder());
		assertEquals("/opt/ibm/idmu/bar", config.getLoadFolder());
		assertEquals(99, config.getNestLimit());
		assertEquals(88, config.getInsertLimit());
		assertEquals("value", config.getEnv("test"));
	}

	@Test
	public void testConfigOptions() throws MergeException {
		Config config = new Config();
		String optString = config.getAllOptions();
		JsonElement options = proxy.fromJSON(optString, JsonElement.class);
		assertTrue(options.isJsonObject());
		assertTrue(options.getAsJsonObject().has("Template"));
		assertTrue(options.getAsJsonObject().has("Template"));
		assertTrue(options.getAsJsonObject().has("Encoding"));
		assertTrue(options.getAsJsonObject().has("Parser"));
		assertTrue(options.getAsJsonObject().has("Enrich"));
		assertTrue(options.getAsJsonObject().has("Insert"));
		assertTrue(options.getAsJsonObject().has("Parse"));
		assertTrue(options.getAsJsonObject().has("Replace"));
		assertTrue(options.getAsJsonObject().has("Save"));
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
		assertEquals(2, config.getNestLimit());
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
	public void testGetEnv3() throws MergeException {
		Config config = new Config();
		try {
			config.getEnv("Foo");
		} catch (MergeException e) {
			// success
			return;
		}
		fail("Merge Exception expected!");
	}

	@Test
	public void testGetEnv4() throws MergeException {
		Config config = new Config();
		config.getEnvVars().put("VCAP_SERVICES", "{\"SERVICE_NAME\":[\"Some Service JSON\"]}");
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
		Config config = new Config();
		config.getEnvVars().put("VCAP_SERVICES", "{\"SERVICE_NAME\":\"Some Service JSON\"}");
		try {
			config.getEnv("VCAP:SERVICE_NAME");
		} catch (MergeException e) {
			// success
			return;
		}
		fail("Merge Exception expected!");
	}

}
