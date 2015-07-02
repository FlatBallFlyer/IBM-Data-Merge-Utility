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
package com.ibm.util.merge;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.log4j.Logger;
import com.ibm.util.merge.Template;

/**
 * Manages an output archive for generated files
 *
 * @author Mike Storey
 * @see Template#saveOutputAs()
 */
final public class ZipFactory {
    // Factory Constants
    public static final int TYPE_ZIP = 1;
    public static final int TYPE_TAR = 2;
    private static final Logger log = Logger.getLogger(ZipFactory.class.getName());
    private final ConcurrentHashMap<String, TarArchiveOutputStream> tarList = new ConcurrentHashMap<String, TarArchiveOutputStream>();
    private final ConcurrentHashMap<String, ZipOutputStream> zipList = new ConcurrentHashMap<String, ZipOutputStream>();
    private final ConcurrentHashMap<String, String> hashList = new ConcurrentHashMap<String, String>();
    private String outputroot = System.getProperty("java.io.tmpdir");

    /**********************************************************************************
     * <p>Close the Zip File and remove from cache</p>
     *
     * @param guid GUID of Zip output
     * @throws MergeException wrapped IOException
     * @throws IOException    Zip File Close Error
     */
    public void writeFile(String guid, String fileName, StringBuilder content, int type) throws IOException {
        // Write the file to the archive
        if (type == ZipFactory.TYPE_ZIP) {
            saveFileToZip(guid, fileName, content);
        } else {
            saveFileToTar(guid, fileName, content);
        }
        // Calculate the MD5 checksum of the file
        MessageDigest message;
        try {
            message = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("missing md5 algorithm");
        }
        message.update(content.toString().getBytes());
        byte[] digest = message.digest();
        StringBuffer checkSum = new StringBuffer();
        for (byte b : digest) {
            checkSum.append(String.format("%02x", b & 0xff));
        }
        // Make sure we have an output hash defined and add the new file to the hash report
        String oldHash = "";
        if (!hashList.containsKey(guid)) {
            hashList.put(guid, "");
        } else {
            oldHash = hashList.get(guid);
        }
        hashList.put(guid, oldHash + "\n" + fileName + "=" + checkSum.toString());
    }

    /**********************************************************************************
     * SaveFileToZip - Make sure we have an open Zip archive, and then write a file to it.
     *
     * @param guid GUID of Zip output
     * @param type - of TYPE_ZIP or TYPE_GZIP
     * @return ZipOutputStream The Zip Output Stream
     * @throws MergeException        - File Not Found
     * @throws FileNotFoundException Unabel to create output zip
     */
    public void saveFileToZip(String guid, String fileName, StringBuilder content) throws IOException {
        ZipOutputStream outputStream = getZipOutputStream(guid);
        ZipEntry entry = new ZipEntry(fileName);
        log.info("Writing Output File " + fileName);
        outputStream.putNextEntry(entry);
        outputStream.write(content.toString().getBytes());
        outputStream.flush();
        outputStream.closeEntry();
    }

    private ZipOutputStream getZipOutputStream(String guid) throws FileNotFoundException {
        if (!zipList.containsKey(guid)) {
            String outputFile = outputroot + "/" + guid;
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedOutputStream output = new BufferedOutputStream(fos);
            ZipOutputStream outputStream = new ZipOutputStream(output);
            zipList.putIfAbsent(guid, outputStream);
            log.info("Created Zip Output " + guid);
        }
        return zipList.get(guid);
    }

    /**********************************************************************************
     * SaveFileToTar - Make sure we have an open tar.gz archive, and then write a file to it.
     *
     * @param guid GUID of Zip output
     * @param type - of TYPE_ZIP or TYPE_GZIP
     * @return ZipOutputStream The Zip Output Stream
     * @throws MergeException        - File Not Found
     * @throws FileNotFoundException Unabel to create output zip
     */
    public void saveFileToTar(String guid, String fileName, StringBuilder content) throws IOException {
        TarArchiveOutputStream outputStream = getTarOutputStream(guid);
        TarArchiveEntry entry = new TarArchiveEntry(fileName);
        entry.setSize(content.length());
        entry.setNames("root", "root");
        log.info("Writing Output File " + fileName);
        outputStream.putArchiveEntry(entry);
        outputStream.write(content.toString().getBytes());
        outputStream.flush();
        outputStream.closeArchiveEntry();
    }

    private TarArchiveOutputStream getTarOutputStream(String guid) throws FileNotFoundException {
        if (!tarList.containsKey(guid)) {
            String outputFile = outputroot + "/" + guid;
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedOutputStream output = new BufferedOutputStream(fos);
            TarArchiveOutputStream outputStream = new TarArchiveOutputStream(output);
            tarList.putIfAbsent(guid, outputStream);
            log.info("Created Tar Output " + guid);
        }
        return tarList.get(guid);
    }

    /**********************************************************************************
     * <p>Close the Archive File and remove from cache</p>
     *
     * @param guid GUID of Zip output
     * @throws MergeException wrapped IOException
     * @throws IOException    Zip File Close Error
     */
    public void closeStream(String guid, int type) {
        if (type == ZipFactory.TYPE_ZIP) {
            if (zipList.containsKey(guid)) {
                ZipOutputStream zos = zipList.get(guid);
                close(zos);
                zipList.remove(guid);
            }
        } else {
            if (tarList.containsKey(guid)) {
                TarArchiveOutputStream tos = tarList.get(guid);
                close(tos);
                tarList.remove(guid);
            }
        }
        hashList.remove(guid);
    }

    private void close(Closeable zos) {
        try {
            zos.close();
        } catch (IOException e) {
            log.error("Could not close " + zos, e);

        }
    }

    /**********************************************************************************
     * Setter for output root (called during Init)
     *
     * @param String the new Root Folder for output files.
     */
    public void setOutputroot(String newRoot) {
        outputroot = newRoot;
    }

    /**********************************************************************************
     * Get the size of the cache
     */
    public int size() {
        return tarList.size() + zipList.size();
    }

    public String getHash(String guid) {
        if (hashList.get(guid) == null) {
            return "";
        }
        return hashList.get(guid);
    }
}