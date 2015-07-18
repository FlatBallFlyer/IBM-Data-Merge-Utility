package com.ibm.util.merge.storage;

import java.io.IOException;
import java.io.OutputStream;

public abstract class Archive {
    private String filePath;
    private OutputStream outputStream;

	public Archive(String fileName) {
		this.setFilePath(fileName);
	}

    public abstract void openOutputStream() throws IOException;
    
    public void writeFile(String entryName, String content, String userName, String groupName) throws IOException {
    	if (this.outputStream == null) { 
    		openOutputStream();
    	}    	
    }
    
    public void closeOutputStream() throws IOException {
    	if (this.outputStream != null) {
    		this.outputStream.close();
    	}
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
