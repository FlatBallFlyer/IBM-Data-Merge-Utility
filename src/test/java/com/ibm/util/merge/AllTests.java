package com.ibm.util.merge;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	ConfigTest.class, 
	MergerTest.class, 
	TemplateCacheTest.class })

public class AllTests {

}
