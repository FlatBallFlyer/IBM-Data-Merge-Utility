/*
 * 
 * Copyright 2015-2017 IBM
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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * Manages ZIP Archives
 * @author Mike Storey
 */
public class ZipArchive extends Archive {

    public ZipArchive() throws MergeException {
    	super();
    	this.setArchiveType(Archive.ARCHIVE_ZIP);
    }

    public ZipArchive(Merger context) throws MergeException {
        super(context);
        this.setArchiveType(Archive.ARCHIVE_ZIP);
    }

	@Override
    public String writeFile(String entryName, String content, String userName, String groupName) throws MergeException {
		try {
			String chksum = super.writeFile(entryName, content, userName, groupName);
			ZipOutputStream outputStream = (ZipOutputStream) this.getOutputStream();
			ZipEntry entry = new ZipEntry(entryName);
			outputStream.putNextEntry(entry);
			outputStream.write(content.getBytes());
			outputStream.flush();
			outputStream.closeEntry();
			return chksum;
		} catch (IOException e) {
			throw new Merge500("IO Error Writing Zip Archive:" + e.getMessage());
		}

    }
    
	@Override
    public void openOutputStream() throws MergeException {
		try {
			FileOutputStream fos = new FileOutputStream(this.getArchiveFile());
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			this.setOutputStream(new ZipOutputStream(bos));
		} catch  (IOException e) {
			throw new Merge500("IO Error Opening Zip output Stream:" + this.getFilePath() + "," + e.getMessage());
		}
    }

}
