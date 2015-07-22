package com.ibm.util.merge.storage;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ibm.util.merge.MergeException;

/**
 *
 */
public class ZipArchive extends Archive {

    public ZipArchive(String filePath) {
    	super(filePath);
    }

	@Override
    public String writeFile(String entryName, String content, String userName, String groupName) throws IOException, MergeException {
		String chksum = super.writeFile(entryName, content, userName, groupName);
        ZipOutputStream outputStream = (ZipOutputStream) this.getOutputStream();
        ZipEntry entry = new ZipEntry(entryName);
        outputStream.putNextEntry(entry);
        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.closeEntry();
        return chksum;
    }
    
	@Override
    public void openOutputStream() throws IOException {
        FileOutputStream fos = new FileOutputStream(getFilePath());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        this.setOutputStream(new ZipOutputStream(bos));
    }

}
