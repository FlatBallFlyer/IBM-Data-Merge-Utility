package com.ibm.util.merge;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	BookmarkTest.class, 
	MergeExceptionTest.class, 
	TemplateFactoryFilePersistTest.class,
	TemplateTest.class, 
	com.ibm.util.merge.directive.AllTests.class,
	com.ibm.util.merge.directive.provider.AllTests.class,
	})
public class AllUnitTests {

}
