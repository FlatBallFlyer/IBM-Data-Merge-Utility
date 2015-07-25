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

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Archive {
    private String filePath;
    private OutputStream outputStream;

	public Archive(String fileName) {
		this.setFilePath(fileName);
	}

    public abstract void openOutputStream() throws IOException;
    
    public String writeFile(String entryName, String content, String userName, String groupName) throws IOException, MergeException {
    	if (this.outputStream == null) { 
    		openOutputStream();
    	}
    	return getCheckSum(content);
    }
    
    public void closeOutputStream() throws IOException {
    	if (this.outputStream != null) {
    		this.outputStream.close();
    	}
    }

    private String getCheckSum(String content) throws MergeException {
    	MessageDigest message;
		try {
			message = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new MergeException(e, "MD5 Algorithm Exception!", content);
		}
		message.update(content.toString().getBytes());
		byte[] digest = message.digest();
		StringBuffer checkSum = new StringBuffer();
		for (byte b : digest) {
			checkSum.append(String.format("%02x", b & 0xff));
		}
		return checkSum.toString();
    }

    public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public OutputStream getOutputStream() {
		return outputStream;
	}
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
