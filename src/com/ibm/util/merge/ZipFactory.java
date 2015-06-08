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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
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
	public static final int TYPE_GZIP = 2;
	private static final Logger log = Logger.getLogger( ZipFactory.class.getName() );
	private static final ConcurrentHashMap<String,Object> archiveList = new ConcurrentHashMap<String,Object>();
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
    	if ( !archiveList.containsKey(guid) ) { 
    		DeflaterOutputStream stream;
    		BufferedOutputStream streamOutput;
    		String outputFile = outputroot + "/" + guid;
			try {
				streamOutput = new BufferedOutputStream(new FileOutputStream(outputFile));
				stream = new ZipOutputStream(streamOutput);	
	    		archiveList.putIfAbsent(guid, stream);
	    		log.info("Created ZipOutput " + guid);
			} catch (FileNotFoundException e) {
				throw new MergeException(e, "FileNotFound creating Output Archive, check merge-output-root parameter in WEB-INF", outputFile);
			}
    	}
    	
    	ZipOutputStream out = (ZipOutputStream) archiveList.get(guid);
		ZipEntry entry = new ZipEntry(fileName);
		try {
			out.putNextEntry(entry);
		    log.info("Writing Output File " + fileName);
		    out.write(content.toString().getBytes()); 
			out.closeEntry();
		} catch (IOException e) {
			throw new MergeException(e, "Error Writing to Zip File", fileName);
		}
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
    	if ( !archiveList.containsKey(guid) ) { 
    		BufferedOutputStream bufferedOutput;
    		GZIPOutputStream gzipStream;
    		TarArchiveOutputStream outputStream;
    		String outputFile = outputroot + "/" + guid;
			try {
				bufferedOutput = new BufferedOutputStream(new FileOutputStream(outputFile));
				gzipStream = new GZIPOutputStream(bufferedOutput);	
				outputStream = new TarArchiveOutputStream(gzipStream);
	    		archiveList.putIfAbsent(guid, outputStream);
	    		log.info("Created TarOutput " + guid);
			} catch (FileNotFoundException e) {
				throw new MergeException(e, "FileNotFound creating Output Archive, check merge-output-root parameter in WEB-INF", outputFile);
			} catch (IOException e) {
				throw new MergeException(e, "Error Creating tar.gz output file, check merge-output-root parameter in WEB-INF", outputFile);
			}
    	}
    	
    	TarArchiveOutputStream out = (TarArchiveOutputStream) archiveList.get(guid);
    	TarArchiveEntry entry = new TarArchiveEntry(fileName, true); 
    	entry.setSize(content.length());
    	entry.setNames("root","root");
		try {
			out.putArchiveEntry(entry);
		    log.info("Writing Output File " + fileName);
		    out.write(content.toString().getBytes()); 
		    out.flush();
			out.closeArchiveEntry();
		} catch (IOException e) {
			throw new MergeException(e, "Error Writing to Zip File", fileName);
		}
    }
    
    /**********************************************************************************
	 * <p>Close the Zip File and remove from cache</p>
	 *
	 * @param  guid GUID of Zip output
     * @throws MergeException wrapped IOException
     * @throws IOException Zip File Close Error
	 */
    public static void closeStream(String guid, int type) throws MergeException {
    	if ( archiveList.containsKey(guid) ) { 
    		if (type == ZipFactory.TYPE_ZIP) {
    			try {
    		    	ZipOutputStream out = (ZipOutputStream) archiveList.get(guid);
    		    	out.close();
    			} catch (IOException e) {
    				throw new MergeException(e, "Error Closing ZIP archive stream", guid);
    			} finally {
    		    	archiveList.remove(guid);
    	    		log.info("Closed Output " + guid);
    			}
    		} else {
		    	try {
		        	TarArchiveOutputStream out = (TarArchiveOutputStream) archiveList.get(guid);
					out.close();
				} catch (IOException e) {
					throw new MergeException(e, "Error Closing Tar archive stream", guid);
				} finally {
			    	archiveList.remove(guid);
		    		log.info("Closed Output " + guid);
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
    	return archiveList.size();
    }
    
}