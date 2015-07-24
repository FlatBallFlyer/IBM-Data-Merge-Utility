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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationTestingJdbcProvider {
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
		// Initialize requestMap (usually from request.getParameterMap())
		parameterMap = new HashMap<>();
	}

	@After
	public void teardown() throws IOException {
		FileUtils.cleanDirectory(outputDir); 
	}
	
	@Test
	public void testDefaultTemplate() throws MergeException, IOException {
		String output = tf.getMergeOutput(parameterMap);
		assertEquals("This is the Default Template", output);
	}

	@Test
	public void testjdbcDefaultDataTar() throws Exception {
		testIt("jdbcDef.functional.", "tar");
	}

	@Test
	public void testjdbcDefaultDataZip() throws Exception {
		testIt("jdbcDef.functional.","zip");
	}

	@Test
	public void testjdbcTagDataTar() throws Exception {
		testIt("jdbcTag.functional.","tar");
	}

	@Test
	public void testjdbcTagDataZip() throws Exception {
		testIt("jdbcTag.functional.", "zip");
	}

	@Test
	public void testjdbcUrlDataTar() throws Exception {
		testIt("jdbcUrl.functional.","tar");
	}

	@Test
	public void testjdbcUrlDataZip() throws Exception {
		testIt("jdbcUrl.functional.","zip");
	}

	private void testIt(String fullName, String type) throws Exception {
		String fileName = fullName + type;
		parameterMap.put("DragonFlyFullName", 	new String[]{fullName});
		parameterMap.put("DragonFlyOutputFile", new String[]{fileName});
		parameterMap.put("DragonFlyOutputType", new String[]{type});
		String output = tf.getMergeOutput(parameterMap);
		assertTrue(output.trim().isEmpty());
		CompareArchives.assertArchiveEquals(type, new File(validateDir, fileName).getAbsolutePath(), new File(outputDir, fileName).getAbsolutePath());
	}
}
