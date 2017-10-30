package com.ibm.util.merge.template.directive.enrich.source;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.directive.enrich.provider.FileSystemProvider;

public class FileSystemSourceTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFileSystemSource() {
		FileSystemSource source = new FileSystemSource();
		assertEquals(AbstractSource.SOURCE_FILE, source.getType());
	}
	
	@Test
	public void testGetNewProvider() throws MergeException {
		FileSystemSource source = new FileSystemSource();
		assertTrue(source.getProvider() instanceof FileSystemProvider);
	}

	@Test
	public void testGetSetFileSystemPath() {
		FileSystemSource source = new FileSystemSource();
		assertEquals("/temp", source.getFileSystemPath());
		source.setFileSystemPath("Foo");
		assertEquals("Foo", source.getFileSystemPath());
	}

}
