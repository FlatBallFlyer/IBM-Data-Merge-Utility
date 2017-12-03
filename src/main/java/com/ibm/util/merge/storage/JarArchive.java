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

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * Manages Jar Archives
 * @author Mike Storey
 *
 */
public class JarArchive extends Archive {

	public JarArchive(Merger context) throws MergeException {
        super(context);
        this.setArchiveType(Archive.ARCHIVE_JAR);
	}

	@Override
    public String writeFile(String entryName, String content, String userName, String groupName) throws MergeException {
		try {
	    	String chksum = super.writeFile(entryName, content, userName, groupName);
	    	JarArchiveOutputStream outputStream = (JarArchiveOutputStream) this.getOutputStream();
	        JarArchiveEntry entry = new JarArchiveEntry(entryName);
	        entry.setSize(content.getBytes().length);
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
	        this.setOutputStream(new JarArchiveOutputStream(bos));
		} catch  (IOException e) {
			throw new Merge500("IO Error Opening Tar output Stream:" + this.getFilePath() + "," + e.getMessage());
		}
    }

}
