package com.ibm.util.merge.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ibm.util.merge.MergeException;

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
