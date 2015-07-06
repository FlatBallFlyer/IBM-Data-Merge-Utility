package com.ibm.util.merge.storage;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class TarFileWriter {
    private static final Logger log = Logger.getLogger(TarFileWriter.class);
    private File filePath;
    private String entryName;
    private String content;
    private String userName;
    private String groupName;

    public TarFileWriter(File filePath, String entryName, String content, String userName, String groupName) {
        this.filePath = filePath;
        this.entryName = entryName;
        this.content = content;
        this.userName = userName;
        this.groupName = groupName;
    }

    public void write() throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        TarArchiveOutputStream outputStream = new TarArchiveOutputStream(bos);
        TarArchiveEntry entry = new TarArchiveEntry(entryName);
        entry.setSize(content.length());
        entry.setNames(userName, groupName);
        outputStream.putArchiveEntry(entry);
        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.closeArchiveEntry();
        outputStream.close();
    }
}
