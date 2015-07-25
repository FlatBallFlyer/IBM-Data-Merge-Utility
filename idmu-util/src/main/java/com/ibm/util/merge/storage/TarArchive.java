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
package com.ibm.util.merge.storage;

import com.ibm.util.merge.MergeException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class TarArchive extends Archive {

    public TarArchive(String filePath) {
        super(filePath);
    }

	@Override
    public String writeFile(String entryName, String content, String userName, String groupName) throws IOException, MergeException {
    	String chksum = super.writeFile(entryName, content, userName, groupName);
    	TarArchiveOutputStream outputStream = (TarArchiveOutputStream) this.getOutputStream();
        TarArchiveEntry entry = new TarArchiveEntry(entryName);
        entry.setSize(content.length());
        entry.setNames(userName, groupName);
        outputStream.putArchiveEntry(entry);
        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.closeArchiveEntry();
        return chksum;
    }
    
	@Override
    public void openOutputStream() throws IOException {
        FileOutputStream fos = new FileOutputStream(getFilePath());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        this.setOutputStream(new TarArchiveOutputStream(bos));
    }

}
