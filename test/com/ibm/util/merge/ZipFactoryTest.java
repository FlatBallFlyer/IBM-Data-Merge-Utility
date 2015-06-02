package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Before;
import org.junit.Test;

public class ZipFactoryTest {
	private Template template;
	@Before
	public void setUp() throws Exception {
		template = TemplateFactory.cacheFromJson("");
	}

	@Test
	public void testGetZipStreamZip() throws MergeException, IOException {
		ZipOutputStream out = ZipFactory.getZipStream("Foo.zip", ZipFactory.TYPE_ZIP);
		assertNotNull(out);
		ZipFactory.closeStream("Foo.zip");
	}

	@Test
	public void testGetZipStreamTar() throws MergeException {
		ZipOutputStream stream1 = ZipFactory.getZipStream("Foo.tar.gz", ZipFactory.TYPE_ZIP);
		assertNotNull(stream1);
		ZipFactory.closeStream("Foo.tar.gz");
	}

}
