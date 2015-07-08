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
    public String getOutputRoot() {
        return outputRoot;
    }
}