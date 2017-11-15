package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.ibm.util.merge.data.parser.DataProxyJson;

public class All_Functional_Tests {
	private TemplateCache cache;
	private Merger context;
	private final DataProxyJson gsonProxy = new DataProxyJson();

	@Test
	public void Simple() throws Throwable {
		testTemplate(new File("src/test/resources/functional/simple"));
	}
	
	@Test
	public void ArgumentsReplace() throws Throwable {
		testTemplate(new File("src/test/resources/functional/argumentsReplace"));
	}
	
	@Test
	public void ReplaceDefaults() throws Throwable {
		testTemplate(new File("src/test/resources/functional/replaceDefaults"));
	}
	
	@Test
	public void ReplaceInsert1() throws Throwable {
		testTemplate(new File("src/test/resources/functional/nestedInsert"));
	}
	
	@Test
	public void wordDoc() throws Throwable {
		testTemplate(new File("src/test/resources/functional/wordDoc"));
	}
	
	@Test
	public void templateUpgrade() throws Throwable {
		//testTemplate(new File("src/test/resources/functional/templateUpgrade"));
	}
	
	@Test
	public void fileProvider() throws Throwable {
		testTemplate(new File("src/test/resources/functional/fileProviderTest"));
	}
	
	@Test
	public void fileProviderOptimized() throws Throwable {
		testTemplate(new File("src/test/resources/functional/fileProviderTestOptimized"));
	}
	
	@Test
	public void testOptimized() throws Throwable {
		Long startFile = System.currentTimeMillis();
		testTemplate(new File("src/test/resources/functional/fileProviderTest"));
		Long endFile = System.currentTimeMillis();

		Long startOpt = System.currentTimeMillis();
		testTemplate(new File("src/test/resources/functional/fileProviderTestOptimized"));
		Long endOpt = System.currentTimeMillis();
		
		assertTrue((endOpt-startOpt) < (endFile-startFile));
	}
	/*
	 * Folder should contain the following files
	 * - templates.json - the testing templates, with the root templaet test..
	 * - parameters.json - json structure with parameters
	 * - payload.txt - the request payload
	 * - config.json - a Config object to use
	 * - output.txt - the expected output
	 * The file can also contain an optional arcive.zip that is matched against archive output.
	 * 
	 */
	public void testTemplate(File folder) throws Throwable {
		assertTrue(folder.exists());
		assertTrue(folder.isDirectory());
		String payload = new String(Files.readAllBytes(new File(folder.getPath() + File.separator + "payload.txt").toPath()), "ISO-8859-1");
		String parms = new String(Files.readAllBytes(new File(folder.getPath() + File.separator + "parameters.json").toPath()), "ISO-8859-1");
		Parameters parameters = gsonProxy.fromString(parms, Parameters.class);
		Config.load(new String(Files.readAllBytes(new File(folder.getPath() + File.separator + "config.json").toPath()), "ISO-8859-1"));
		cache = new TemplateCache();
		context = new Merger(cache, "test..", parameters.parms, payload);
		String output = context.merge().getMergeContent().getValue();
		assertEquals(new String(Files.readAllBytes(new File(folder.getPath() + File.separator + "output.txt").toPath()), "ISO-8859-1"), output);
		
		File archive = new File(folder.getPath() + File.separator + "archive.zip");
		File generated = new File(context.getArchive().getArchiveFile().getAbsolutePath());
		if (archive.exists() && !generated.exists()) {
			fail("Merge did not generate expected archive!");
		}
		if (generated.exists()) {
			assertTrue(archive.exists());
			assertArchiveEquals(archive.getAbsolutePath(), context.getArchive().getArchiveFile().getAbsolutePath());
			generated.delete();
		}
	}
	
    public static final void assertArchiveEquals(String archive1, String archive2) throws Throwable {
        // Get Archives
        ZipFile zipFile1 = new ZipFile(new File(archive1));
        ZipFile zipFile2 = new ZipFile(new File(archive2));

        // Get Member Hash
        HashMap<String, ZipEntry> files1 = getMembers(zipFile1);
        HashMap<String, ZipEntry> files2 = getMembers(zipFile2);

        // Compare Files
        assertMembersEqual(zipFile1, files1, zipFile2, files2);
    }

    /**
     * @param archive
     * @return
     * @throws IOException
     */
    private static final HashMap<String, ZipEntry> getMembers(ZipFile archive) throws IOException {
        HashMap<String, ZipEntry> map = new HashMap<String, ZipEntry>();
        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) archive.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            map.put(entry.getName(), entry);
        }
        return map;
    }

    /**
     * @param zip1
     * @param files1
     * @param zip2
     * @param files2
     * @throws IOException
     */
    private static final void assertMembersEqual(ZipFile zip1, HashMap<String, ZipEntry> files1, 
                                                 ZipFile zip2, HashMap<String, ZipEntry> files2) throws IOException {
        if (files1.size() != files2.size()) {
            fail("Different Sizes, expected " + Integer.toString(files1.size()) + " found " + Integer.toString(files2.size()));
        }

        for (String key : files1.keySet()) {
            if (!files2.containsKey(key)) {
                fail("Expected file not in target " + key);
            }
            String file1 = IOUtils.toString(zip1.getInputStream(files1.get(key)));
            String file2 = IOUtils.toString(zip2.getInputStream(files2.get(key)));
            assertEquals(file1, file2);
        }
    }

	private class Parameters {
    	public Map<String, String[]> parms;
    }
}
