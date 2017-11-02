package com.ibm.util.merge.template.directive.enrich.provider;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CacheProviderTest.class,
	StubProviderTest.class, 
	FileSystemProviderTest.class
	})
public class AllUnitTests {

}
