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

import org.apache.log4j.Logger;

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
//    private final ConcurrentHashMap<String, TarArchiveOutputStream> tarList = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, ZipOutputStream> zipList = new ConcurrentHashMap<>();
//    public final ConcurrentHashMap<String, String> hashList = new ConcurrentHashMap<>();
    private String outputRoot = System.getProperty("java.io.tmpdir");

    /**********************************************************************************
     * Setter for output root (called during Init)
     *
     * @param String the new Root Folder for output files.
     */
    public void setOutputRoot(String newRoot) {
        outputRoot = newRoot;
    }

    /**********************************************************************************
     * Get the size of the cache
     */
//    public int size() {
//        return tarList.size() + zipList.size();
//    }

    public String getOutputRoot() {
        return outputRoot;
    }

//    public ConcurrentHashMap<String, TarArchiveOutputStream> getTarList() {
//        return tarList;
//    }
//
//    public ConcurrentHashMap<String, ZipOutputStream> getZipList() {
//        return zipList;
//    }
//
//    public ConcurrentHashMap<String, String> getHashList() {
//        return hashList;
//    }
}