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
import com.ibm.util.merge.CompareArchives;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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
	public void testWriteFileZip() throws MergeException, IOException, NoSuchAlgorithmException {
		assertEquals(0, ZipFactory.size());
		makeArchive("test/output/", "test1.zip", ZipFactory.TYPE_ZIP);
		assertEquals(0, ZipFactory.size());
		CompareArchives.assertZipEquals("test/valid/test1.zip", "test/output/test1.zip");
	}

	@Test
	public void testWriteFileTar() throws MergeException, IOException, NoSuchAlgorithmException {
		assertEquals(0, ZipFactory.size());
		makeArchive("test/output/", "test1.tar", ZipFactory.TYPE_TAR);
		assertEquals(0, ZipFactory.size());
		CompareArchives.assertTarEquals("test/valid/test1.tar", "test/output/test1.tar");
	}

	private void makeArchive(String outputRoot, String outputFile, int type) throws MergeException {
		ZipFactory.setOutputroot(outputRoot);
		ZipFactory.writeFile(outputFile, "path/file1.txt", new StringBuilder("Test Output File One"), type);
		ZipFactory.writeFile(outputFile, "path/file2.txt", new StringBuilder("Test Output File Two"), type);
		ZipFactory.closeStream(outputFile, type);
	}

}
