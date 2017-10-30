package com.ibm.util.merge.storage;

import static org.junit.Assert.*;

import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Config;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.TemplateCache;
import com.ibm.util.merge.exception.MergeException;

public class JarArchiveTest {
	private Config config;
	private TemplateCache cache;
	private Merger context;
	private JarArchive archive;

	@Before
	public void setUp() throws Exception {
		config = new Config();
		cache = new TemplateCache(config);
		context = new Merger(cache, config, "system.sample.");
		archive = new JarArchive(context);
		archive.setFilePath("");
	}

	@Test
	public void testJarArchive() {
		assertNotEquals(null, archive);
		assertEquals(Archive.ARCHIVE_JAR, archive.getArchiveType());
	}
	
	@Test
	public void testOpenOutputStream() throws MergeException {
		assertNull(archive.getOutputStream());
		archive.openOutputStream();
		assertTrue(archive.getOutputStream() instanceof JarArchiveOutputStream);
		assertNotEquals(null, archive.getOutputStream());
		archive.closeOutputStream();
		assertTrue(archive.getArchiveFile().exists());
		archive.getArchiveFile().delete();
		assertFalse(archive.getArchiveFile().exists());
	}

	@Test
	public void testWriteFile() throws MergeException {
		assertNull(archive.getOutputStream());
		archive.writeFile("entry", "content", "user", "group");
		assertNotNull(archive.getOutputStream());
		archive.closeOutputStream();
		JarArchiveOutputStream outputStream = (JarArchiveOutputStream) archive.getOutputStream();
		assertNotEquals(null, outputStream);
		assertTrue(archive.getArchiveFile().exists());
		archive.getArchiveFile().delete();
		assertFalse(archive.getArchiveFile().exists());
	}

}
