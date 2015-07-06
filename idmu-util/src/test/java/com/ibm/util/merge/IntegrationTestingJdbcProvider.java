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

import com.ibm.util.merge.persistence.FilesystemPersistence;
import junitx.framework.FileAssert;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;


public class IntegrationTestingJdbcProvider {
	HashMap<String, String[]> parameterMap;
	String templateDir 	= "integration/templates/";
	String outputDir 	= "integration/output/"; 
	String validateDir 	= "integration/valid/";

	private RuntimeContext rtc;

	@Before
	public void setup() throws Exception {
		rtc = TestUtils.createDefaultRuntimeContext();
		// Initialize Factories
		TemplateFactory tf = rtc.getTemplateFactory();
		tf.reset();

		tf.loadTemplatesFromFilesystem();
		rtc.getZipFactory().setOutputRoot(outputDir);
		parameterMap = new HashMap<>();
	}

	@Test
	public void testDefaultTemplate() throws Exception {
		TemplateFactory tf = rtc.getTemplateFactory();
		Template root = tf.getTemplate(parameterMap);
		root.merge(rtc);
		final String returnValue;
		if (!root.canWrite()) {
			returnValue = "";
		} else {
			returnValue = root.getContent();
		}
		root.doWrite(rtc.getZipFactory());
		String output = returnValue;
//		root.packageOutput(zf, cf);
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + "merge1.output"))), output);
	}

	@Test
	public void testCsvDefaultDataTar() throws Exception {
//		testIt("csvDef.functional.", "tar");
		testIt("csvDef.SMTP.", ".tar");
	}

	@Test
	public void testCsvDefaultDataZip() throws Exception {
		testIt("csvDef.functional.","zip");
	}

	@Test
	public void testCsvTagDataTar() throws Exception {
		testIt("csvTag.functional.","tar");
	}

	@Test
	public void testCsvTagDataZip() throws Exception {
		testIt("csvTag.functional.", "zip");
	}

	@Test
	public void testCsvUrlDataTar() throws Exception {
		testIt("csvUrl.functional.","tar.gz");
	}

	@Test
	public void testCsvUrlDataZip() throws Exception {
		testIt("csvUrl.functional.","zip");
	}

	/**
	 * @param fullName
	 * @param type
	 * @throws MergeException
	 * @throws IOException
	 */
	private void testIt(String fullName, String type) throws Exception {
		parameterMap.put(Template.TAG_OUTPUT_TYPE, 		new String[]{type});
		parameterMap.put(Template.TAG_OUTPUTFILE, 		new String[]{fullName+type});
		testMerge(fullName);
		FileAssert.assertBinaryEquals(new File(validateDir + fullName + "." + type),new File(outputDir + fullName + "." + type));
	}

	/**
	 * @param fullName
	 * @throws MergeException
	 * @throws IOException
	 */
	private void testMerge(String fullName) throws Exception {
		TemplateFactory tf = rtc.getTemplateFactory();
		parameterMap.put(tf.KEY_FULLNAME, 	new String[]{fullName});
		Template root = tf.getTemplate(parameterMap);
		root.merge(rtc);
		final String returnValue;
		if (!root.canWrite()) {
			returnValue = "";
		} else {
			returnValue = root.getContent();
		}
		root.doWrite(rtc.getZipFactory());
		String mergeOutput = returnValue;
//		root.packageOutput(zf, cf);
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + fullName + ".output"))), mergeOutput);
	}
}
