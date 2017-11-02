package com.ibm.util.merge.template.directive.enrich.provider;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	CloudantProviderTest.class,
	JdbcProviderTest.class,
	JndiProviderTest.class, 
	MongoProviderTest.class, 
	RestProviderTest.class, 
	})
public class AllIntegrationTests {

}
