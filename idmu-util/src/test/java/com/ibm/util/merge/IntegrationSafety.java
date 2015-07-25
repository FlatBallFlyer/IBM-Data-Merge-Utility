/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.util.merge;

import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.AbstractPersistence;
import com.ibm.util.merge.persistence.FilesystemPersistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class IntegrationSafety {
	HashMap<String, String[]> parameterMap;
	File templateDir 	= new File("src/test/resources/templates/");
	File outputDir 		= new File("src/test/resources/testout/");
	File validateDir 	= new File("src/test/resources/valid/");
    JsonProxy jsonProxy = new PrettyJsonProxy();
    AbstractPersistence persist = new FilesystemPersistence(templateDir, jsonProxy);
    ConnectionPoolManager manager = new ConnectionPoolManager();
    TemplateFactory tf 	= new TemplateFactory(persist, jsonProxy, outputDir, manager);

	@Before
	public void setup() throws MergeException, IOException {
		parameterMap = new HashMap<String, String[]>();
	}

	@After
	public void teardown() throws IOException {
	}
	
	@Test
	public void testSafetyInsert() throws NoSuchAlgorithmException, IOException  {
		parameterMap.put("DragonFlyFullName", 	new String[]{"safety.insert."});
		String expected = "<html><head></head><body><p>A Merge Execption has occured: Insert Subs Infinite Loop suspected <br/> ";
		String output = tf.getMergeOutput(parameterMap);
		assertEquals(expected, output.substring(0, expected.length()));
	}

	@Test
	public void testSafetyReplace() throws Exception{
		parameterMap.put("DragonFlyFullName", 	new String[]{"safety.replace."});
		String expected = "Test Replace To contains From {FROM}";
		String output = tf.getMergeOutput(parameterMap);
		assertEquals(expected, output);
	}
}
