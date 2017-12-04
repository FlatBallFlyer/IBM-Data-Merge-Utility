package com.ibm.util.merge.storage;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.Cache;
import com.ibm.util.merge.data.DataManager;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;

public class ArchiveTest {
	private class AbstractTest extends Archive {
		
		public AbstractTest(Merger context) throws MergeException {
			super(context);
		}
		
		@Override
		public void openOutputStream() throws MergeException {
			ByteArrayOutputStream  buffer =  new ByteArrayOutputStream();
			this.setOutputStream(buffer);
		}
		
		@Override
	    public String writeFile(String entryName, String content, String userName, String groupName) throws MergeException {
			try {
		    	String chksum = super.writeFile(entryName, content, userName, groupName);
		    	ByteArrayOutputStream outputStream = (ByteArrayOutputStream) this.getOutputStream();
		    	outputStream.write(content.getBytes());
		        outputStream.flush();
	    	return chksum;
			} catch (IOException e) {
				throw new Merge500("IO Error Writing Tar Archive:" + e.getMessage());
			}
	    }
		
	}
	private Cache cache;
	private Merger context;
	Archive archive;
	
	@Before
	public void setUp() throws Exception {
		cache = new Cache();
		context = new Merger(cache, "system.Merge500.");
		archive = new AbstractTest(context);
	}

	@Test
	public void testArchive() {
		assertFalse(archive.getFileName().isEmpty());
	}

	@Test
	public void testOpenWriteCloseStream() throws MergeException {
		assertEquals(null, archive.getOutputStream());
		archive.writeFile("filename1", "SomeContent", "user", "group");
		archive.writeFile("filename2", "MoreContent", "user", "group");
		assertNotEquals(null, archive.getOutputStream());
		archive.closeOutputStream();
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) archive.getOutputStream();
		assertEquals("SomeContentMoreContent", outputStream.toString());
		
		DataManager data = context.getMergeData();
		assertTrue(data.contians(Merger.IDMU_ARCHIVE, "-"));
		assertTrue(data.contians(Merger.IDMU_ARCHIVE_FILES, "-"));
		assertTrue(data.contians(Merger.IDMU_ARCHIVE_OUTPUT, "-"));
		assertTrue(data.get(Merger.IDMU_ARCHIVE_FILES, "-").isList());
		assertEquals("filename1", data.get(Merger.IDMU_ARCHIVE_FILES + "-[0]-name", "-").getAsPrimitive());
		assertEquals("filename2", data.get(Merger.IDMU_ARCHIVE_FILES + "-[1]-name", "-").getAsPrimitive());
	}

	@Test
	public void testGetSetFilePath() {
		archive.setFilePath("Foo");
		assertEquals("Foo",archive.getFilePath());
	}

	@Test
	public void testGetSetFileName() {
		archive.setFileName("Foo");
		assertEquals("Foo",archive.getFileName());
	}

	@Test
	public void testGetSetArchiveType() {
		for (String type : Archive.ARCHIVE_TYPES()) {
			archive.setArchiveType(type);
			assertEquals(type, archive.getArchiveType());
		}
		archive.setArchiveType(Archive.ARCHIVE_GZIP);
		assertEquals(Archive.ARCHIVE_GZIP, archive.getArchiveType());
		archive.setArchiveType("Foo");
		assertEquals(Archive.ARCHIVE_GZIP, archive.getArchiveType());
	}

	@Test
	public void testGetSetOutputStream() throws MergeException {
		ByteArrayOutputStream  buffer =  new ByteArrayOutputStream();
		archive.setOutputStream(buffer);
		assertSame(buffer, archive.getOutputStream());
	}
	
	

}
