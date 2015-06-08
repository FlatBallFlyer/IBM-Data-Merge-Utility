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

import junitx.framework.FileAssert;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;


public class FunctionalCsvTest {
	Template root = null;
	String output = "";
	HashMap<String, String[]> parameterMap;
	String templateDir 	= "integration/templates/";
	String outputDir 	= "integration/output/"; 
	String validateDir 	= "integration/valid/";
	
	@Before
	public void setup() throws MergeException, IOException {
		// Initialize Factories
		TemplateFactory.reset();
		TemplateFactory.setDbPersistance(false);
		TemplateFactory.setTemplateFolder(templateDir);
		TemplateFactory.loadAll();
		ZipFactory.setOutputroot(outputDir);
		// Initialize requestMap (usually from request.getParameterMap())
		parameterMap = new HashMap<String,String[]>();
	}

	@Test
	public void testDefaultTemplate() throws MergeException, IOException {
		root = TemplateFactory.getTemplate(parameterMap);
		output = root.merge();
		root.packageOutput();
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + "merge1.output"))), output);
	}

	@Test
	public void testCsvDefaultDataTarGz() throws MergeException, IOException {
		String fullName = "csvDef.functional.";
		parameterMap.put(TemplateFactory.KEY_FULLNAME, 	new String[]{fullName + "."});
		parameterMap.put(Template.TAG_OUTPUTFILE, 		new String[]{fullName});
		root = TemplateFactory.getTemplate(parameterMap);
		output = root.merge();
		root.packageOutput();
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + fullName + ".output"))), output);
		FileAssert.assertBinaryEquals(new File(validateDir + fullName + ".tar.gz"),new File(outputDir + fullName + ".tar.gz"));
	}

	@Test
	public void testCsvDefaultDataZip() throws MergeException, IOException {
		String fullName = "csvDef.functional.";
		parameterMap.put(TemplateFactory.KEY_FULLNAME, 	new String[]{fullName + "."});
		parameterMap.put(Template.TAG_OUTPUT_TYPE, 		new String[]{"zip"});
		parameterMap.put(Template.TAG_OUTPUTFILE, 		new String[]{fullName});
		root = TemplateFactory.getTemplate(parameterMap);
		output = root.merge();
		root.packageOutput();
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + fullName + ".output"))), output);
		FileAssert.assertBinaryEquals(new File(validateDir + fullName + ".zip"),new File(outputDir + fullName + ".zip"));
	}

	@Test
	public void testCsvTagDataTarGz() throws MergeException, IOException {
		String fullName = "csvTag.functional.";
		parameterMap.put(TemplateFactory.KEY_FULLNAME, 	new String[]{fullName + "."});
		parameterMap.put(Template.TAG_OUTPUTFILE, 		new String[]{fullName});
		root = TemplateFactory.getTemplate(parameterMap);
		output = root.merge();
		root.packageOutput();
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + fullName + ".output"))), output);
		FileAssert.assertBinaryEquals(new File(validateDir + fullName + ".tar.gz"),new File(outputDir + fullName + ".tar.gz"));
	}

	@Test
	public void testCsvTagDataZip() throws MergeException, IOException {
		String fullName = "csvTag.functional.";
		parameterMap.put(TemplateFactory.KEY_FULLNAME, 	new String[]{fullName + "."});
		parameterMap.put(Template.TAG_OUTPUT_TYPE, 		new String[]{"zip"});
		parameterMap.put(Template.TAG_OUTPUTFILE, 		new String[]{fullName});
		root = TemplateFactory.getTemplate(parameterMap);
		output = root.merge();
		root.packageOutput();
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + fullName + ".output"))), output);
		FileAssert.assertBinaryEquals(new File(validateDir + fullName + ".zip"),new File(outputDir + fullName + ".zip"));
	}

	@Test
	public void testCsvUrlDataTarGz() throws MergeException, IOException {
		String fullName = "csvUrl.functional.";
		parameterMap.put(TemplateFactory.KEY_FULLNAME, 	new String[]{fullName + "."});
		parameterMap.put(Template.TAG_OUTPUTFILE, 		new String[]{fullName});
		root = TemplateFactory.getTemplate(parameterMap);
		output = root.merge();
		root.packageOutput();
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + fullName + ".output"))), output);
		FileAssert.assertBinaryEquals(new File(validateDir + fullName + ".tar.gz"),new File(outputDir + fullName + ".tar.gz"));
	}

	@Test
	public void testCsvUrlDataZip() throws MergeException, IOException {
		String fullName = "csvUrl.functional.";
		parameterMap.put(TemplateFactory.KEY_FULLNAME, 	new String[]{fullName + "."});
		parameterMap.put(Template.TAG_OUTPUT_TYPE, 		new String[]{"zip"});
		parameterMap.put(Template.TAG_OUTPUTFILE, 		new String[]{fullName});
		root = TemplateFactory.getTemplate(parameterMap);
		output = root.merge();
		root.packageOutput();
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + fullName + ".output"))), output);
		FileAssert.assertBinaryEquals(new File(validateDir + fullName + ".zip"),new File(outputDir + fullName + ".zip"));
	}

}
