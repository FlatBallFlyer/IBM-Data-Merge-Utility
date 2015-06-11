package com.ibm.util.merge;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	IntegrationSafety.class, 
	IntegrationTestingCsvProvider.class, 
	IntegrationTestingHtmlProvider.class,
//	IntegrationTestingCsvProvider.class, 
	})
public class AllUnitTests {

}
