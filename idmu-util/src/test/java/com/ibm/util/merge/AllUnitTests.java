package com.ibm.util.merge;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	BookmarkTest.class, 
	MergeExceptionTest.class, 
	TemplateFactoryTest.class,
	TemplateTest.class, 
	ArchiveWriterTest.class,
	com.ibm.util.merge.directive.AllTests.class,
	com.ibm.util.merge.directive.provider.AllTests.class,
//	ConnectionFactoryTest.class
	})
public class AllUnitTests {

}
