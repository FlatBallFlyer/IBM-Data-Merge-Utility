package com.ibm.util.merge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class All_Performance_Tests {

	@Test
	public void MySql1() throws Throwable {
		// Given Customer ID as a parameter - create Contacts Report with Corporate Info
		testTemplatePerformance(new File("src/test/resources/performance/JsonXml"));
	}

	/**
	 * Performance test Transformation Templates
	 * Templates should have test.aToB. base template that transform files in format A (in payload) to format B 
	 * and test.bToA.base template that transforms in the other direction.
	 * All "A" files are tested with "B" reverse (Files in B without a corresponding A will not be tested) 
	 * @param folder
	 * @throws Throwable
	 */
	public void testTemplatePerformance(File folder ) throws Throwable {
		int repeatCount = 25;
		assertTrue(folder.exists());
		assertTrue(folder.isDirectory());
		
		// Load the configuration
		File config = new File(folder.getPath() + File.separator + "config.json");
		assertTrue(config.exists());
		Config.load(new String(Files.readAllBytes(config.toPath()), "ISO-8859-1"));
		
		// Create the template cache and post the templates
		Cache cache = new Cache(folder);

		// Load a/b List
		File aFolder = new File(folder.getPath() + File.separator + "a");
		assertTrue(aFolder.exists());
		assertTrue(aFolder.isDirectory());
		File bFolder = new File(folder.getPath() + File.separator + "b");
		assertTrue(bFolder.exists());
		assertTrue(bFolder.isDirectory());
		
		// Biuld list of requests
		ArrayList<TestUnit> units = new ArrayList<TestUnit>();
		File[] tests = aFolder.listFiles();
		for (File test : tests) {
			if (test.isFile() && !test.getName().startsWith(".")) {
				TestUnit unit = new TestUnit();
				File a = new File(test.getPath());
				assertTrue(a.exists());
				File b = new File(bFolder.getPath() + File.separator + a.getName());
				assertTrue(b.exists());
				unit.a = new String(Files.readAllBytes(a.toPath()), "ISO-8859-1");
				unit.b = new String(Files.readAllBytes(b.toPath()), "ISO-8859-1");
				units.add(unit);
			}
		}

		// Process all A to B requests
		long time = 0;
		long count = 0;
		long start = 0;
		long end = 0;
		Merger context;
		for (TestUnit unit : units) {
			for (int x = 0; x < repeatCount; x++) {
				// Get a Merger and Perform the A to B Merge 
				start = System.currentTimeMillis();
				context = new Merger(cache, "test.aToB.", new HashMap<String, String[]>(), unit.a);
				unit.aToB = context.merge().getMergeContent().getValue();
				end = System.currentTimeMillis();
				time += (end-start); count++;
			}
		}
		// Log performance
		System.out.println("Performance for AtoB in ".concat(folder.getPath()));
		System.out.print("Test Count: "); System.out.println(count);
		System.out.print(" Test Time: "); System.out.println(time);
		System.out.print("  Avg Resp: "); System.out.println(time/count);
		
		// Process all B to A requests
		time = 0;
		count = 0;
		for (TestUnit unit : units) {
			for (int x = 0; x < repeatCount; x++) {
				// Get a Merger and Perform the A to B Merge 
				start = System.currentTimeMillis();
				context = new Merger(cache, "test.bToA.", new HashMap<String, String[]>(), unit.b);
				unit.bToA = context.merge().getMergeContent().getValue();
				end = System.currentTimeMillis();
				time += (end-start); count++;
			}
		}
		// Log performance
		System.out.println("Performance for BtoA in ".concat(folder.getPath()));
		System.out.print("Test Count: "); System.out.println(count);
		System.out.print(" Test Time: "); System.out.println(time);
		System.out.print("  Avg Resp: "); System.out.println(time/count);

		// Verify Results
		for (TestUnit unit : units) {
			assertEquals(unit.a, unit.bToA);
			assertEquals(unit.b, unit.aToB);
		}		
	}
	private class TestUnit {
		public String a;
		public String b;
		public String aToB;
		public String bToA;
	}
}