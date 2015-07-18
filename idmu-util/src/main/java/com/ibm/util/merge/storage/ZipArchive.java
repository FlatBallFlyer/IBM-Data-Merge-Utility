package com.ibm.util.merge.storage;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
public class ZipArchive extends Archive {
    private ZipOutputStream outputStream;

    public ZipArchive(String filePath) {
    	super(filePath);
    }

	@Override
    public void writeFile(String entryName, String content, String userName, String groupName) throws IOException {
    	if (this.outputStream == null) { 
    		openOutputStream();
    	}
        ZipEntry entry = new ZipEntry(entryName);
        this.outputStream.putNextEntry(entry);
        this.outputStream.write(content.getBytes());
        this.outputStream.flush();
        this.outputStream.closeEntry();
    }
    
    private void openOutputStream() throws IOException {
        FileOutputStream fos = new FileOutputStream(getFilePath());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        this.outputStream = new ZipOutputStream(bos);
    }
    
	@Override
    public void closeOutputStream() throws IOException {
    	if (this.outputStream != null) {
    		this.outputStream.close();
    	}
    }

}
