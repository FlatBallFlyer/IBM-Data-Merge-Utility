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
import com.ibm.idmu.api.PoolManagerConfiguration;
import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.TemplatePersistence;
import com.ibm.util.merge.persistence.FilesystemPersistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class IntegrationTestingJdbcProvider {
	HashMap<String, String[]> parameterMap;
	File templateDir 	= new File("src/test/resources/templates/");
	File outputDir 		= new File("src/test/resources/testout/");
	File validateDir 	= new File("src/test/resources/valid/");
    File jdbcProperties = new File("src/test/resources/properties/databasePools.properties"); 
    JsonProxy jsonProxy = new PrettyJsonProxy();
    TemplatePersistence persist = new FilesystemPersistence(templateDir, jsonProxy);
    ConnectionPoolManager poolManager = new ConnectionPoolManager();
    PoolManagerConfiguration config = PoolManagerConfiguration.fromPropertiesFile(jdbcProperties);
    
    TemplateFactory tf 	= new TemplateFactory(persist, jsonProxy, outputDir, poolManager);

	@Before
	public void setup() throws MergeException, IOException {
		// Initialize requestMap (usually from request.getParameterMap())
	    poolManager.applyConfig(config);
		parameterMap = new HashMap<>();
	}

	@After
	public void teardown() throws IOException {
//		FileUtils.cleanDirectory(outputDir); 
	}
	
	@Test
	public void testDefaultTemplate() throws MergeException, IOException {
		String output = tf.getMergeOutput(parameterMap);
		assertEquals("This is the Default Template", output);
	}

	@Test
	public void testjdbcDataTar() throws Exception {
		testIt("jdbc.functional.", "tar");
	}

	@Test
	public void testjdbcDataZip() throws Exception {
		testIt("jdbc.functional.","zip");
	}

	private void testIt(String fullName, String type) throws Exception {
		String fileName = fullName + type;
		parameterMap.put("DragonFlyFullName", 	new String[]{fullName});
		parameterMap.put("DragonFlyOutputFile", new String[]{fileName});
		parameterMap.put("DragonFlyOutputType", new String[]{type});
		String output = tf.getMergeOutput(parameterMap);
		assertEquals("", output.trim());
		CompareArchives.assertArchiveEquals(type, new File(validateDir, fileName).getAbsolutePath(), new File(outputDir, fileName).getAbsolutePath());
	}
}
