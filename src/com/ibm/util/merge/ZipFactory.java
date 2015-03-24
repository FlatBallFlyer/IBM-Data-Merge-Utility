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
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.ibm.util.merge.Template;

/**
 * Manages an output archive for generated files
 *
 * @see Template#saveAs()
 * @author  Mike Storey
 */
final class ZipFactory {
	// Factory Constants
	private static final Logger log = Logger.getLogger( ZipFactory.class.getName() );
	private static final ConcurrentHashMap<String,ZipOutputStream> archiveList = new ConcurrentHashMap<String,ZipOutputStream>();
	private static String outputroot = System.getProperty("java.io.tmpdir");
	
    /**********************************************************************************
	 * <p>Zip Factory</p>
	 *
	 * @param  guid GUID of Zip output
	 * @return ZipOutputStream The Zip Output Stream
     * @throws FileNotFoundException Unabel to create output zip
	 */
    public static ZipOutputStream getZipStream(String guid) throws FileNotFoundException {
    	if ( !archiveList.containsKey(guid) ) { 
    		FileOutputStream newFile = new FileOutputStream(outputroot + "/" + guid);
    		ZipOutputStream newStream = new ZipOutputStream(new BufferedOutputStream(newFile));
    		archiveList.putIfAbsent(guid, newStream);
    		log.info("Created ZipOutput " + guid);
    	}
    	return archiveList.get(guid);
    }
    
    /**********************************************************************************
	 * <p>Close the Zip File and remove from cache</p>
	 *
	 * @param  guid GUID of Zip output
     * @throws IOException Zip File Close Error
	 */
    public static void closeStream(String guid) throws IOException {
    	if ( archiveList.containsKey(guid) ) { 
	    	archiveList.get(guid).close();
	    	archiveList.remove(guid);
    		log.info("Closed ZipOutput " + guid);
    	}
    }
    
    /**********************************************************************************
	 * <p>Close the Zip File and remove from cache</p>
	 *
	 * @param  guid GUID of Zip output
     * @throws IOException Zip File Close Error
	 */
    public static void setOutputroot(String newRoot) {
    	outputroot = newRoot;
    }
    
}