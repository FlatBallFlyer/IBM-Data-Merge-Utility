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
 * @author flatballflyer
 *
 */
/**
 * @author flatballflyer
 *
 */
public abstract class Archive {
	public static final String ARCHIVE_TYPE			= "ARCHIVE_TYPE";
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
		enums.put(ARCHIVE_TYPE, ARCHIVE_TYPES());
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
	 * @param context
	 */
	public Archive(Merger context) {
		this();
		this.setContext(context);
	}

    /**
     * Open the output stream
     * @throws MergeException
     */
    public abstract void openOutputStream() throws MergeException;
    
    /**
     * Write a file to the archive
     * @param entryName
     * @param content
     * @param userName
     * @param groupName
     * @return
     * @throws MergeException
     */
    public String writeFile(String entryName, String content, String userName, String groupName) throws MergeException {
    	if (this.outputStream == null) { 
    		openOutputStream();
    	}
    	return getCheckSum(content, entryName);
    }
    
    /**
     * Close the output stream
     * @throws MergeException
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
     * @param content
     * @param name
     * @return
     * @throws MergeException
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
    	DataObject entry = new DataObject();
    	entry.put("name", new DataPrimitive(name));
    	entry.put("size", new DataPrimitive(content.getBytes().length));
    	entry.put("MD5",  new DataPrimitive(md5));
    	this.context.getMergeData().put(Merger.IDMU_ARCHIVE_FILES, "-", entry);
		return md5;
    }
    
    /**
     *
     * @return Archive file 
     */
    public File getArchiveFile() {
    	File file = new File(File.pathSeparator + this.filePath + File.pathSeparator + this.fileName + "." + this.archiveType);
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
	 * @param archiveType
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
	 * @param filePath
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
	 * @param outputStream
	 * @throws MergeException
	 */
	public void setOutputStream(OutputStream outputStream) throws MergeException {
		this.outputStream = outputStream;
		DataManager data = context.getMergeData();
		data.put(Merger.IDMU_ARCHIVE, "-", 			gson.fromJSON(gson.toJson(this), DataObject.class));
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
	 * @param fileName
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
