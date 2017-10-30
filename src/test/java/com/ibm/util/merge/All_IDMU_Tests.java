package com.ibm.util.merge;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	com.ibm.util.merge.AllTests.class,
	com.ibm.util.merge.data.AllTests.class,
	com.ibm.util.merge.template.directive.AllTests.class,
	com.ibm.util.merge.data.parser.AllTests.class,
	com.ibm.util.merge.template.directive.enrich.provider.AllTests.class,
	com.ibm.util.merge.template.directive.enrich.source.AllTests.class,
	com.ibm.util.merge.storage.AllTests.class,
	com.ibm.util.merge.template.AllTests.class,
})

public class All_IDMU_Tests {

}

