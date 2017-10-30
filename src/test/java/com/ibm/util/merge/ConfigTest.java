package com.ibm.util.merge;

import static org.junit.Assert.*;

import org.junit.*;

import com.ibm.util.merge.exception.Merge500;

public class ConfigTest {
	
	@Test
	public void testConfigDefault() throws Merge500 {
		Config config = new Config();
	}

	@Test
	public void testConfigString() throws Merge500 {
		String configString = 
				"{\"tempFolder\": \"/opt/ibm/idmu/temp\",\"maxStack\": 99}";
		Config config = new Config(configString);
		assertEquals("/opt/ibm/idmu/temp", config.getTempFolder());
	}

	@Test
	public void testConfigEnvironment() throws Merge500 {
		Config config = new Config("");
	}

	@Test
	public void testGetSetTempFolder() throws Merge500 {
		Config config = new Config();
		config.setTempFolder("Foo");
		assertEquals("Foo", config.getTempFolder());
	}

}
