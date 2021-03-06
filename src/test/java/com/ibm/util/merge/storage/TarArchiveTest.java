package com.ibm.util.merge.storage;

import static org.junit.Assert.*;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.exception.MergeException;

public class TarArchiveTest {
	private Cache cache;
	private Merger context;
	private TarArchive archive;

	@Before
	public void setUp() throws Exception {
		Config config = new Config("{\"tempFolder\":\"src/test/resources/temp\"}");
		cache = new Cache(config);
		context = new Merger(cache, "system.sample.");
		archive = new TarArchive(context);
		archive.setFilePath("");
	}

	@Test
	public void testTarArchive() {
		assertNotEquals(null, archive);
		assertEquals(Archive.ARCHIVE_TAR, archive.getArchiveType());
	}
	
	@Test
	public void testOpenOutputStream() throws MergeException {
		assertNull(archive.getOutputStream());
		archive.setFilePath("src/test/resources/temp");
		archive.openOutputStream();
		assertTrue(archive.getOutputStream() instanceof TarArchiveOutputStream);
		assertNotEquals(null, archive.getOutputStream());
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
		TarArchiveOutputStream outputStream = (TarArchiveOutputStream) archive.getOutputStream();
		assertNotEquals(null, outputStream);
		assertTrue(archive.getArchiveFile().exists());
		archive.getArchiveFile().delete();
		assertFalse(archive.getArchiveFile().exists());
	}

}
