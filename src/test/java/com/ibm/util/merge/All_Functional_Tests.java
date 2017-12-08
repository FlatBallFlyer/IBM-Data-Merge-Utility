package com.ibm.util.merge;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.Test;

public class All_Functional_Tests {
	private final MergeTestHarness harness = new MergeTestHarness();

	@Test
	public void Simple() throws Throwable {
		harness.testTemplates(new File("src/test/resources/functional/simple"));
	}
	
	@Test
	public void ArgumentsReplace() throws Throwable {
		harness.testTemplates(new File("src/test/resources/functional/argumentsReplace"));
	}
	
	@Test
	public void ReplaceDefaults() throws Throwable {
		harness.testTemplates(new File("src/test/resources/functional/replaceDefaults"));
	}
	
	@Test
	public void ReplaceInsert1() throws Throwable {
		harness.testTemplates(new File("src/test/resources/functional/nestedInsert"));
	}
	
	@Test
	public void wordDoc() throws Throwable {
		harness.testTemplates(new File("src/test/resources/functional/wordDoc"));
	}
	
	@Test
	public void templateUpgrade() throws Throwable {
		//harness.testTemplates(new File("src/test/resources/functional/templateUpgrade"));
	}
	
	@Test
	public void fileProvider() throws Throwable {
		harness.testTemplates(new File("src/test/resources/functional/fileProviderTest"));
	}
	
	@Test
	public void replaceOveride() throws Throwable {
		harness.testTemplates(new File("src/test/resources/functional/insertListPrimitives"));
	}
	
	@Test
	public void fileProviderOptimized() throws Throwable {
		harness.testTemplates(new File("src/test/resources/functional/fileProviderTestOptimized"));
	}
	
	@Test
	public void testOptimized() throws Throwable {
		Long startFile = System.currentTimeMillis();
		harness.testTemplates(new File("src/test/resources/functional/fileProviderTest"));
		Long endFile = System.currentTimeMillis();

		Long startOpt = System.currentTimeMillis();
		harness.testTemplates(new File("src/test/resources/functional/fileProviderTestOptimized"));
		Long endOpt = System.currentTimeMillis();
		
		assertTrue((endOpt-startOpt) < (endFile-startFile));
	}
}
