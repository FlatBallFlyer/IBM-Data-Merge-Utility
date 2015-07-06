package com.ibm.util.merge.storage;

import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
public class ZipFileWriter {
    private static final Logger log = Logger.getLogger(ZipFileWriter.class);
    private File filePath;
    private String entryName;
    private String content;

    public ZipFileWriter(File filePath, String entryName, String content) {
        this.filePath = filePath;
        this.entryName = entryName;
        this.content = content;
    }

    public void write() throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ZipOutputStream outputStream = new ZipOutputStream(bos);
        ZipEntry entry = new ZipEntry(entryName);
        outputStream.putNextEntry(entry);
        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.closeEntry();
        outputStream.close();
    }
}
