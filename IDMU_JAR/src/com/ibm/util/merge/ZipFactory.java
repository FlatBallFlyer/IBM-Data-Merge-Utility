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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
 * @see Template#saveOutputAs()
 * @author  Mike Storey
 */
final public class ZipFactory {
	// Factory Constants
	public static final int TYPE_ZIP = 1;
	public static final int TYPE_TAR = 2;
	private static final Logger log = Logger.getLogger( ZipFactory.class.getName() );
	private static final ConcurrentHashMap<String,TarArchiveOutputStream> tarList = new ConcurrentHashMap<String,TarArchiveOutputStream>();
	private static final ConcurrentHashMap<String,ZipOutputStream> zipList = new ConcurrentHashMap<String,ZipOutputStream>();
	private static String outputroot = System.getProperty("java.io.tmpdir");
    /**********************************************************************************
	 * <p>Close the Zip File and remove from cache</p>
	 *
	 * @param  guid GUID of Zip output
     * @throws MergeException wrapped IOException
     * @throws IOException Zip File Close Error
	 */
    public static void writeFile(String guid, String fileName, StringBuilder content, int type) throws MergeException {
    	if (type == ZipFactory.TYPE_ZIP) {
    		saveFileToZip(guid, fileName, content);
    	} else {
    		saveFileToTar(guid, fileName, content);
		}
    }
	
    /**********************************************************************************
	 * SaveFileToZip - Make sure we have an open Zip archive, and then write a file to it.
	 *
	 * @param  guid GUID of Zip output
	 * @param  type - of TYPE_ZIP or TYPE_GZIP
	 * @return ZipOutputStream The Zip Output Stream
     * @throws MergeException - File Not Found
     * @throws FileNotFoundException Unabel to create output zip
	 */
    public static void saveFileToZip(String guid, String fileName, StringBuilder content) throws MergeException  {
    	ZipOutputStream outputStream = getZipOutputStream(guid);
		ZipEntry entry = new ZipEntry(fileName);
		try {
		    log.info("Writing Output File " + fileName);
			outputStream.putNextEntry(entry);
		    outputStream.write(content.toString().getBytes()); 
		    outputStream.flush();
		    outputStream.closeEntry();
		} catch (IOException e) {
			throw new MergeException(e, "Error Writing to Zip File", fileName);
		}
    }

    private static ZipOutputStream getZipOutputStream(String guid) throws MergeException {
    	if ( !zipList.containsKey(guid) ) { 
    		String outputFile = outputroot + "/" + guid;
			try {
				BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
				ZipOutputStream outputStream = new ZipOutputStream(output);				
				zipList.putIfAbsent(guid, outputStream);
	    		log.info("Created Zip Output " + guid);
			} catch (FileNotFoundException e) {
				throw new MergeException(e, "FileNotFound creating Output Archive, check merge-output-root parameter in WEB-INF", outputFile);
			}
    	}
    	return zipList.get(guid);
    }
    
    /**********************************************************************************
	 * SaveFileToTar - Make sure we have an open tar.gz archive, and then write a file to it.
	 *
	 * @param  guid GUID of Zip output
	 * @param  type - of TYPE_ZIP or TYPE_GZIP
	 * @return ZipOutputStream The Zip Output Stream
     * @throws MergeException - File Not Found
     * @throws FileNotFoundException Unabel to create output zip
	 */
    public static void saveFileToTar(String guid, String fileName, StringBuilder content) throws MergeException  {
    	TarArchiveOutputStream outputStream = getTarOutputStream(guid);
    	TarArchiveEntry entry = new TarArchiveEntry(fileName); 
    	entry.setSize(content.length());
    	entry.setNames("root","root");
		try {
		    log.info("Writing Output File " + fileName);
			outputStream.putArchiveEntry(entry);
		    outputStream.write(content.toString().getBytes()); 
		    outputStream.flush();
		    outputStream.closeArchiveEntry();
		} catch (IOException e) {
			throw new MergeException(e, "Error Writing to Zip File", fileName);
		}
    }
    
    private static TarArchiveOutputStream getTarOutputStream(String guid) throws MergeException {
    	if ( !tarList.containsKey(guid) ) { 
    		String outputFile = outputroot + "/" + guid;
			try {
				BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
				TarArchiveOutputStream outputStream = new TarArchiveOutputStream(output);				
	    		tarList.putIfAbsent(guid, outputStream);
	    		log.info("Created Tar Output " + guid);
			} catch (FileNotFoundException e) {
				throw new MergeException(e, "FileNotFound creating Output Archive, check merge-output-root parameter in WEB-INF", outputFile);
			}
    	}
    	return tarList.get(guid);
    }

    /**********************************************************************************
	 * <p>Close the Archive File and remove from cache</p>
	 *
	 * @param  guid GUID of Zip output
     * @throws MergeException wrapped IOException
     * @throws IOException Zip File Close Error
	 */
    public static void closeStream(String guid, int type) throws MergeException {
    	if ( type == ZipFactory.TYPE_ZIP) {
        	if ( zipList.containsKey(guid) ) {
        		try {
        			zipList.get(guid).close();
        			zipList.remove(guid);
    			} catch (IOException e) {
    				throw new MergeException(e, "IO Exception Closing Output Stream", guid);
    			}
        	} 
    		
    	} else {
	    	if ( tarList.containsKey(guid) ) {
	    		try {
	    			tarList.get(guid).close();
	    			tarList.remove(guid);
				} catch (IOException e) {
					throw new MergeException(e, "IO Exception Closing Output Stream", guid);
				}
	    	}
    	}
    }
    
    /**********************************************************************************
	 * Setter for output root (called during Init)
	 * @param String the new Root Folder for output files.
	 */
    public static void setOutputroot(String newRoot) {
    	outputroot = newRoot;
    }
    
    /**********************************************************************************
	 * Get the size of the cache
	 */
    public static int size() {
    	return tarList.size() + zipList.size();
    }
    
}