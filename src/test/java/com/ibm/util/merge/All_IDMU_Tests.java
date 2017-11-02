package com.ibm.util.merge;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	com.ibm.util.merge.AllTests.class,
	com.ibm.util.merge.data.AllTests.class,
	com.ibm.util.merge.data.parser.AllTests.class,
	com.ibm.util.merge.storage.AllTests.class,
	com.ibm.util.merge.template.AllTests.class,
	com.ibm.util.merge.template.content.AllTests.class,
	com.ibm.util.merge.template.directive.AllTests.class,
	com.ibm.util.merge.template.directive.enrich.provider.AllUnitTests.class
})

public class All_IDMU_Tests {

}

