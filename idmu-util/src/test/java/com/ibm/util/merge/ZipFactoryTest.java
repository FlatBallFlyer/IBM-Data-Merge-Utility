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

import com.ibm.util.merge.storage.TarFileWriter;
import com.ibm.util.merge.storage.ZipFileWriter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ZipFactoryTest {
    private Logger log = Logger.getLogger(ZipFactoryTest.class);
    private ZipFactory zf;

    @Before
    public void setup() throws IOException {
        zf = new ZipFactory();
        FileUtils.cleanDirectory(new File("src/test/resources/testout"));
    }

    @After
    public void teardown() throws IOException {
        FileUtils.cleanDirectory(new File("src/test/resources/testout"));
    }

    @Test
    public void testWriteFileZip() throws MergeException, IOException, NoSuchAlgorithmException {
        makeArchive("src/test/resources/testout/", "test1.zip", ZipFactory.TYPE_ZIP);
        CompareArchives.assertZipEquals("src/test/resources/valid/test1.zip", "src/test/resources/testout/test1.zip");
    }

    @Test
    public void testWriteFileTar() throws MergeException, IOException, NoSuchAlgorithmException {
        makeArchive("src/test/resources/testout/", "test1.tar", ZipFactory.TYPE_TAR);
        CompareArchives.assertTarEquals("src/test/resources/valid/test1.tar", "src/test/resources/testout/test1.tar");
    }

    private void makeArchive(String outputRoot, String outputFile, int type) throws IOException {
        zf.setOutputRoot(outputRoot);
        final StringBuilder content = new StringBuilder("Test Output File One");
        String entryPath = "path/file2.txt";
        File outputFile1 = new File(zf.getOutputRoot() + "/" + outputFile);
        if (type == ZipFactory.TYPE_ZIP) {
            new ZipFileWriter(outputFile1, entryPath, content.toString()).write();
        } else {
            new TarFileWriter(outputFile1, entryPath, content.toString(), "root", "root").write();
        }
    }
}
