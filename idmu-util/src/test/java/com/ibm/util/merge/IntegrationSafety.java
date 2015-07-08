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

import com.ibm.util.merge.template.Template;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class IntegrationSafety {
	HashMap<String, String[]> parameterMap;
	String templateDir 	= "src/test/resources/templates/";
	String outputDir 	= "src/test/resources/testout/"; 
	String validateDir 	= "src/test/resources/valid/";

	private RuntimeContext rtc;

	@Before
	public void setup() throws MergeException, IOException {
		rtc = TestUtils.createRuntimeContext(outputDir);
		// Initialize Factories
		TemplateFactory tf = rtc.getTemplateFactory();
		tf.reset();
		tf.loadTemplatesFromFilesystem();
//		zf.setOutputRoot(outputDir);
		
		// Reset the output directory
		FileUtils.cleanDirectory(new File(outputDir)); 
		parameterMap = new HashMap<>();
	}

	@After
	public void teardown() throws IOException {
		FileUtils.cleanDirectory(new File(outputDir)); 
	}
	
	@Test
	public void testSafetyInsert() throws NoSuchAlgorithmException, IOException  {
		try {
			testIt("safety.insert.", "tar");
		} catch (MergeException e) {
			return;
		}
		fail("Safety Insert failed to throw exception");
	}

	@Test(expected = RuntimeException.class)
	public void testSafetyReplace() throws Exception{

			parameterMap.put("FROM",	new String[]{"Safety Replace {FROM} Value"});
			testIt("safety.replace.", "tar");

		fail("Safety Replace failed to throw exception");
	}

	@Test
	public void testInsertTagAsList() throws NoSuchAlgorithmException, IOException, MergeException  {
		parameterMap.put("Words",	new String[]{"One, Two, Three, Four and Five"});
		assertEquals("The words are: One, - Two, - Three, - Four and Five. -", testIt("root.list.", "tar"));
	}

	/**
	 * @param fullName
	 * @param type
	 * @throws MergeException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	private String testIt(String fullName, String type) throws MergeException, IOException, NoSuchAlgorithmException {
		parameterMap.put("DragonOutputType", 	new String[]{type});
		parameterMap.put("DragonFlyOutputFile", new String[]{fullName+type});
		parameterMap.put("DragonFlyFullName", 	new String[]{fullName});
		TemplateFactory tf = rtc.getTemplateFactory();
		Template root = tf.getTemplate(parameterMap);
		root.merge(rtc);
		final String returnValue;
		if (!root.canWrite()) {
			returnValue = "";
		} else {
			returnValue = root.getContent();
		}
		tf.getFs().doWrite(root);
//		root.doWrite(zf);
		String output = returnValue;
//		root.packageOutput(zf, cf);
		return output;
	}
}
