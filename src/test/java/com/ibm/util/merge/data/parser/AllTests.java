package com.ibm.util.merge.data.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	ParserTest.class, 
	DataProxyCsvTest.class, 
	DataProxyHtmlTest.class,
	DataProxyJsonTest.class, 
	DataProxyXmlStrictTest.class })
public class AllTests {

}
