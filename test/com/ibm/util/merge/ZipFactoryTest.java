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

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

public class ZipFactoryTest {

	@Test
	public void testGetZipStreamZip() throws MergeException, IOException {
		ZipOutputStream out = ZipFactory.getZipStream("Foo.zip", ZipFactory.TYPE_ZIP);
		assertNotNull(out);
		assertEquals(1, ZipFactory.size());
		ZipFactory.closeStream("Foo.zip");
		assertEquals(0, ZipFactory.size());
	}

	@Test
	public void testGetZipStreamTar() throws MergeException {
		ZipOutputStream stream1 = ZipFactory.getZipStream("Foo.tar.gz", ZipFactory.TYPE_ZIP);
		assertNotNull(stream1);
		assertEquals(1, ZipFactory.size());
		ZipFactory.closeStream("Foo.tar.gz");
		assertEquals(0, ZipFactory.size());
	}

}
