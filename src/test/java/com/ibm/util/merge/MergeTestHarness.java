package com.ibm.util.merge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.ibm.util.merge.data.parser.DataProxyJson;

public class MergeTestHarness {
	private final DataProxyJson gsonProxy = new DataProxyJson(false);

	public MergeTestHarness() {
	}
	
	/*
	 * Folder should contain the following files
	 * - config.json - a Config object to use
	 * - templates.json - the testing templates, with the root template test..
	 * - A directory called requests with sub-directories that each contain the following files 
	 * - parameters.json - json structure with parameters
	 * - payload.txt - the request payload
	 * - output.txt - the expected output
	 * - archive.zip - if an archive output is required - only Zip is supported
	 * The file can also contain an optional arcive.zip that is matched against archive output.
	 * 
	 */
	public void testTemplates(File folder) throws Throwable {
		assertTrue(folder.exists());
		assertTrue(folder.isDirectory());
		
		// Load the configuration
		File config = new File(folder.getPath() + File.separator + "config.json");
		assertTrue(config.exists());
		Config.load(new String(Files.readAllBytes(config.toPath()), "ISO-8859-1"));
		
		// Create the template cache and post the templates
		TemplateCache cache = new TemplateCache(folder);

		// Iterate over requests performing the merge and validating output
		File testFolder = new File(folder.getPath() + File.separator + "requests");
		assertTrue(testFolder.exists());
		assertTrue(testFolder.isDirectory());
		
		// Biuld list of requests
		ArrayList<TestUnit> units = new ArrayList<TestUnit>();
		File[] tests = testFolder.listFiles();
		for (File test : tests) {
			if (test.isDirectory()) {
				TestUnit unit = new TestUnit();
				
				File payload = new File(test.getPath() + File.separator + "payload.txt");
				assertTrue(payload.exists());
				unit.payload = new String(Files.readAllBytes(payload.toPath()), "ISO-8859-1");
				
				File parms = new File(test.getPath() + File.separator + "parameters.json");
				String theParms = new String(Files.readAllBytes(parms.toPath()), "ISO-8859-1");
				unit.parameters = gsonProxy.fromString(theParms, Parameters.class);
	
				File output = new File(test.getPath() + File.separator + "output.txt");
				assertTrue(output.exists());
				unit.output = new String(Files.readAllBytes(output.toPath()), "ISO-8859-1");
	
				unit.archive = new File(test.getPath() + File.separator + "archive.zip");
				units.add(unit);
			}
		}
		
		// Process all requests
		long time = 0;
		long count = 0;
		for (TestUnit unit : units) {
			Long start = System.currentTimeMillis();
			// Get a Merger and Perform the Merge - validate expected output
			Merger context = new Merger(cache, "test..", unit.parameters.parms, unit.payload);
			String theOutput = context.merge().getMergeContent().getValue();
			assertEquals(unit.output, theOutput);
			Long end = System.currentTimeMillis();
			time += (end-start); count++;
			
			// If an archive is expected (or generated) perform archive equals assertion
			File generated = new File(context.getArchive().getArchiveFile().getAbsolutePath());
			if (unit.archive.exists() && !generated.exists()) {
				fail("Merge did not generate expected archive!");
			}
			if (generated.exists()) {
				assertTrue(unit.archive.exists());
				this.assertEqual(unit.archive.getAbsolutePath(), context.getArchive().getArchiveFile().getAbsolutePath());
				generated.delete();
			}
		}
		System.out.println("Performance for ".concat(folder.getPath()));
		System.out.print("Test Count: "); System.out.println(count);
		System.out.print(" Test Time: "); System.out.println(time);
		System.out.print("  Avg Resp: "); System.out.println(time/count);
	}
	class TestUnit {
		public String payload;
		public Parameters parameters;
		public String output;
		public File archive;
	}


	public void assertEqual(String archive1, String archive2) throws Throwable {
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
    private HashMap<String, ZipEntry> getMembers(ZipFile archive) throws IOException {
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
    @SuppressWarnings("deprecation")
	private void assertMembersEqual(ZipFile zip1, HashMap<String, ZipEntry> files1, 
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
