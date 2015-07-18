package com.ibm.util.merge.storage;

import java.io.IOException;

public abstract class Archive {
    private String filePath;

	public Archive(String fileName) {
		this.setFilePath(fileName);
	}
    public abstract void writeFile(String entryName, String content, String userName, String groupName) throws IOException;
    
    public abstract void closeOutputStream() throws IOException;

    public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
    
}
