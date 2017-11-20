/*
 * 
 * Copyright 2015-2017 IBM
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
package com.ibm.util.merge.storage;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

/**
 * Abstract base class for output archives.
 * 
 * @author Mike Storey
 *
 */
public abstract class Archive {
//	public static final String ARCHIVE_TYPE			= "ARCHIVE_TYPE";
	public static final String ARCHIVE_ZIP			= "zip";
	public static final String ARCHIVE_TAR		 	= "tar";
	public static final String ARCHIVE_JAR		 	= "jar";
	public static final String ARCHIVE_GZIP			= "gzip";
	public static final HashSet<String> ARCHIVE_TYPES() {
		HashSet<String> values = new HashSet<String>();
		values.add(ARCHIVE_ZIP);
		values.add(ARCHIVE_TAR);
		values.add(ARCHIVE_JAR);
		values.add(ARCHIVE_GZIP);
		return values;
	}

	public static final HashMap<String, HashSet<String>> ENUMS() {
		HashMap<String, HashSet<String>> enums = new HashMap<String, HashSet<String>>(); 
		enums.put("Archive Type", ARCHIVE_TYPES());
		return enums;
	}

	private transient DataProxyJson gson = new DataProxyJson();
	private transient Merger context;
	private String archiveType;
	private String filePath;
	private String fileName;
    private transient OutputStream outputStream;

	/**
	 * Instantiate an archive
	 */
	public Archive() {
		this.setFileName(UUID.randomUUID().toString());
	}

	/**
	 * Instantiate an Archive for the merge
	 * @param context The merge context
	 */
	public Archive(Merger context) {
		this();
		this.setContext(context);
	}

    /**
     * Open the output stream
     * @throws MergeException on processing errors
     */
    public abstract void openOutputStream() throws MergeException;
    
    /**
     * Write a file to the archive
     * @param entryName The file name to use
     * @param content The content to write
     * @param userName The user name for the file
     * @param groupName The group name for the file
     * @return The Checksum value
     * @throws MergeException on processing errors
     */
    public String writeFile(String entryName, String content, String userName, String groupName) throws MergeException {
    	if (this.outputStream == null) { 
    		openOutputStream();
    	}
    	return getCheckSum(content, entryName);
    }
    
    /**
     * Close the output stream
     * @throws MergeException on processing errors
     */
    public void closeOutputStream() throws MergeException {
    	if (this.outputStream != null) {
    		try {
				this.outputStream.close();
			} catch (IOException e) {
				throw new Merge500("IO Error Closing Archive:" + this.filePath);			}
    	}
    }

    /**
     * Get CheckSum information for the archive entry
     * @param content The content being checked
     * @param name The name of the digest entry
     * @return The checksum string
     * @throws MergeException on processing errors
     */
    private String getCheckSum(String content, String name) throws MergeException {
    	MessageDigest message;
		try {
			message = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new Merge500("MD5 Algorithm Exception!");
		}
		message.update(content.toString().getBytes());
		byte[] digest = message.digest();
		StringBuffer checkSum = new StringBuffer();
		for (byte b : digest) {
			checkSum.append(String.format("%02x", b & 0xff));
		}
		String md5 = checkSum.toString();
		
		// Add entry to archive files data entry
		if (!this.context.getMergeData().contians(Merger.IDMU_ARCHIVE_FILES, "-")) {
			this.context.getMergeData().put(Merger.IDMU_ARCHIVE_FILES, "-", new DataList());
		}
    	DataObject entry = new DataObject();
    	entry.put("name", new DataPrimitive(name));
    	entry.put("size", new DataPrimitive(content.getBytes().length));
    	entry.put("MD5",  new DataPrimitive(md5));
    	this.context.getMergeData().get(Merger.IDMU_ARCHIVE_FILES, "-").getAsList().add(entry);
		return md5;
    }
    
    /**
     * @return Archive file 
     * @throws MergeException on processing errors
     */
    public File getArchiveFile() throws MergeException {
    	File file = new File(this.filePath + "/" + this.fileName + "." + this.archiveType);
    	return file;
    }

	/**
	 * @return Archive Type
	 */
	public String getArchiveType() {
		return archiveType;
	}

	/**
	 * Set Archive type
	 * @param archiveType The archive type
	 */
	public void setArchiveType(String archiveType) {
		if (Archive.ARCHIVE_TYPES().contains(archiveType)) {
			this.archiveType = archiveType;
		}
	}

	/**
	 * @return File Path for output
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Set Output File Path
	 * @param filePath The file path for output files
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return Output Stream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Set the Output Stream
	 * @param outputStream The output stream 
	 * @throws MergeException on processing errors
	 */
	public void setOutputStream(OutputStream outputStream) throws MergeException {
		this.outputStream = outputStream;
		DataManager data = context.getMergeData();
		data.put(Merger.IDMU_ARCHIVE, "-", 			gson.fromString(gson.toString(this), DataObject.class));
		data.put(Merger.IDMU_ARCHIVE_OUTPUT, "-", new DataPrimitive(this.getArchiveFile().getPath()));
		data.put(Merger.IDMU_ARCHIVE_FILES, "-", new DataList());
	}

	/**
	 * @return File Name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Set the output file name
	 * @param fileName The File Name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Merger getContext() {
		return context;
	}

	public void setContext(Merger context) {
		this.context = context;
	}
}
