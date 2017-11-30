package com.ibm.util.merge.storage;

import static org.junit.Assert.*;

import java.util.zip.ZipOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.exception.MergeException;

public class ZipArchiveTest {
	private Cache cache;
	private Merger context;
	private ZipArchive archive;

	@Before
	public void setUp() throws Exception {
		Config.initialize();
		cache = new Cache();
		context = new Merger(cache, "system.sample.");
		archive = new ZipArchive(context);
	}

	@Test
	public void testZipArchive() {
		assertNotEquals(null, archive);
		assertEquals(Archive.ARCHIVE_ZIP, archive.getArchiveType());
	}
	
	@Test
	public void testOpenOutputStream() throws MergeException {
		assertNull(archive.getOutputStream());
		archive.setFilePath("src/test/resources/temp");
		archive.openOutputStream();
		assertTrue(archive.getOutputStream() instanceof ZipOutputStream);
		archive.closeOutputStream();
		assertTrue(archive.getArchiveFile().exists());
		archive.getArchiveFile().delete();
		assertFalse(archive.getArchiveFile().exists());
		
	}

	@Test
	public void testWriteFile() throws MergeException {
		assertNull(archive.getOutputStream());
		archive.setFilePath("src/test/resources/temp");
		archive.writeFile("entry", "content", "user", "group");
		assertNotNull(archive.getOutputStream());
		archive.closeOutputStream();
		assertTrue(archive.getArchiveFile().exists());
		archive.getArchiveFile().delete();
		assertFalse(archive.getArchiveFile().exists());
	}


}
