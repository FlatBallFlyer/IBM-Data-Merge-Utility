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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import junitx.framework.FileAssert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZipFactoryTest {

	@Before
	public void setup() throws IOException {
		FileUtils.cleanDirectory(new File("test/output/")); 
	}
	
	@After
	public void teardown() throws IOException {
		FileUtils.cleanDirectory(new File("test/output/")); 
	}
	
	@Test
	public void testWriteFileZip() throws MergeException, IOException {
		assertEquals(0, ZipFactory.size());
		makeArchive("test/output/", "test1.zip", ZipFactory.TYPE_ZIP);
		assertEquals(0, ZipFactory.size());
		FileAssert.assertBinaryEquals(new File("test/valid/test1.zip"),new File("test/output/test1.zip"));
	}

	@Test
	public void testWriteFileTar() throws MergeException {
		assertEquals(0, ZipFactory.size());
		makeArchive("test/output/", "test1.tar.gz", ZipFactory.TYPE_GZIP);
		assertEquals(0, ZipFactory.size());
		FileAssert.assertBinaryEquals(new File("test/valid/test1.tar.gz"),new File("test/output/test1.tar.gz"));
	}

	private void makeArchive(String outputRoot, String outputFile, int type) throws MergeException {
		ZipFactory.setOutputroot(outputRoot);
		ZipFactory.writeFile(outputFile, "path/file1.txt", new StringBuilder("Test Output File One"), type);
		ZipFactory.writeFile(outputFile, "path/file2.txt", new StringBuilder("Test Output File Two"), type);
		ZipFactory.closeStream(outputFile, type);
	}

}
