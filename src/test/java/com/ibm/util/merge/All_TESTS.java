package com.ibm.util.merge;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	com.ibm.util.merge.All_Unit_Tests.class,
	com.ibm.util.merge.All_Functional_Tests.class,
	com.ibm.util.merge.All_Integration_Tests.class,
	com.ibm.util.merge.All_Performance_Tests.class
})

public class All_TESTS {

}
