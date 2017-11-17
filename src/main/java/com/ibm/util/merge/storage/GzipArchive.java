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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Manages GZIP Archives
 * @author Mike Storey
 *
 */
public class GzipArchive extends Archive {

    public GzipArchive() {
        super();
        this.setArchiveType(Archive.ARCHIVE_GZIP);
    }

    public GzipArchive(Merger context) {
        super(context);
        this.setArchiveType(Archive.ARCHIVE_GZIP);
    }

	@Override
    public String writeFile(String entryName, String content, String userName, String groupName) throws MergeException {
		try {
			// TODO - Add gZip compression to tar archive code (can we extend?)
	    	String chksum = super.writeFile(entryName, content, userName, groupName);
	    	TarArchiveOutputStream outputStream = (TarArchiveOutputStream) this.getOutputStream();
	        TarArchiveEntry entry = new TarArchiveEntry(entryName);
	        entry.setSize(content.getBytes().length);
	        entry.setNames(userName, groupName);
	        outputStream.putArchiveEntry(entry);
	        outputStream.write(content.getBytes());
	        outputStream.flush();
	        outputStream.closeArchiveEntry();
        return chksum;
		} catch (IOException e) {
			throw new Merge500("IO Error Writing Tar Archive:" + e.getMessage());
		}
    }
    
	@Override
    public void openOutputStream() throws MergeException {
		try {
	        FileOutputStream fos = new FileOutputStream(this.getArchiveFile());
	        BufferedOutputStream bos = new BufferedOutputStream(fos);
	        GZIPOutputStream gos = new GZIPOutputStream(bos);
	        this.setOutputStream(new TarArchiveOutputStream(gos));
		} catch  (IOException e) {
			throw new Merge500("IO Error Opening Tar output Stream:" + this.getFilePath() + "," + e.getMessage());
		}
    }

}
