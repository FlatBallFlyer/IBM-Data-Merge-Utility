package com.ibm.util.merge.template.directive;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	AbstractDataDirectiveTest.class,
	AbstractDirectiveTest.class,
	EnrichTest.class,
	InsertTest.class,
	ParseDataTest.class,
	ReplaceTest.class,
	SaveFileTest.class })
public class AllTests {

}
