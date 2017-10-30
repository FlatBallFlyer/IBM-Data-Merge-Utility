package com.ibm.util.merge.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	PathPartTest.class, 
	PathTest.class,
	DataPrimitiveTest.class,
	DataListTest.class,
	DataObjectTest.class,
	DataManagerTest.class, 
	})
public class AllTests {

}
