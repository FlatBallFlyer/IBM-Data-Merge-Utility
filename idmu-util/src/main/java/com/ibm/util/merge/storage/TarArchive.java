package com.ibm.util.merge.storage;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class TarArchive extends Archive {
    private static final Logger log = Logger.getLogger(TarArchive.class);
    private TarArchiveOutputStream outputStream = null;

    public TarArchive(String archiveFileName) {
        super(archiveFileName);
    }

    public void writeFile(String entryName, String content, String userName, String groupName) throws IOException {
    	if (this.outputStream == null) {
    		openOutputStream();
    	}
        TarArchiveEntry entry = new TarArchiveEntry(entryName);
        entry.setSize(content.length());
        entry.setNames(userName, groupName);
        outputStream.putArchiveEntry(entry);
        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.closeArchiveEntry();
    }
    
    private void openOutputStream() throws IOException {
        FileOutputStream fos = new FileOutputStream(getFilePath());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        this.outputStream = new TarArchiveOutputStream(bos);
    }

    public void closeOutputStream() throws IOException {
    	if (this.outputStream != null) {
            this.outputStream.close();
    	}
    }
}
