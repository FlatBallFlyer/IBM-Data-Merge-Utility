package com.ibm.util.merge.storage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	ArchiveTest.class, 
	GzipArchiveTest.class, 
	TarArchiveTest.class,
	JarArchiveTest.class,
	ZipArchiveTest.class 
	})
public class AllTests {

}
