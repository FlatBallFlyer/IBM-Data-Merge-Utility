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
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.ibm.util.merge.Template;

/**
 * Manages an output archive for generated files
 *
 * @see Template#saveOutputAs()
 * @author  Mike Storey
 */
final class ZipFactory {
	// Factory Constants
	public static final int TYPE_ZIP = 1;
	public static final int TYPE_GZIP = 2;
	private static final Logger log = Logger.getLogger( ZipFactory.class.getName() );
	private static final ConcurrentHashMap<String,ZipOutputStream> archiveList = new ConcurrentHashMap<String,ZipOutputStream>();
	private static String outputroot = System.getProperty("java.io.tmpdir");
	
    /**********************************************************************************
	 * <p>Zip Factory</p>
	 *
	 * @param  guid GUID of Zip output
	 * @param  type - of TYPE_ZIP or TYPE_GZIP
	 * @return ZipOutputStream The Zip Output Stream
     * @throws MergeException - File Not Found
     * @throws FileNotFoundException Unabel to create output zip
	 */
    public static ZipOutputStream getZipStream(String guid, int type) throws MergeException  {
    	if ( !archiveList.containsKey(guid) ) { 
    		ZipOutputStream stream;
    		BufferedOutputStream streamOutput;
    		String outputFile = outputroot + "/" + guid;
			try {
				streamOutput = new BufferedOutputStream(new FileOutputStream(outputFile));
				stream = new ZipOutputStream(streamOutput);
				if (type == TYPE_GZIP) {
					stream = new ZipOutputStream(new GZIPOutputStream(stream));
				}
	    		archiveList.putIfAbsent(guid, stream);
	    		log.info("Created ZipOutput " + guid);
			} catch (FileNotFoundException e) {
				throw new MergeException(e, "FileNotFound creating Output Archive, check merge-output-root parameter in WEB-INF", outputFile);
			} catch (IOException e) {
				throw new MergeException(e, "I/O Exception creating Output Archive, check merge-output-root parameter in WEB-INF", outputFile);
			}
    	}
    	return archiveList.get(guid);
    }
    
    /**********************************************************************************
	 * <p>Close the Zip File and remove from cache</p>
	 *
	 * @param  guid GUID of Zip output
     * @throws MergeException wrapped IOException
     * @throws IOException Zip File Close Error
	 */
    public static void closeStream(String guid) throws MergeException {
    	if ( archiveList.containsKey(guid) ) { 
	    	try {
				archiveList.get(guid).close();
			} catch (IOException e) {
				throw new MergeException(e, "Error Closing archive stream", guid);
			} finally {
		    	archiveList.remove(guid);
	    		log.info("Closed ZipOutput " + guid);
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
    
}