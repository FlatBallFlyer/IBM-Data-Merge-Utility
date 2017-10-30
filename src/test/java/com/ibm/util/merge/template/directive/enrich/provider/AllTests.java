package com.ibm.util.merge.template.directive.enrich.provider;
/*
 * Special Testing Requirements:
 * VCAP_SERVICES Environment Variable
{"idmu-mongo1": [{"credentials":{"username": "user","password": "password","host": "localhost","port": "80","url": "connectstring"}}],
 "idmu-cloud1":	[{"credentials":{"username": "user","password": "password","host": "localhost","port": "80","url": ""}}],
 "compose-for-mysql":[{"credentials":{}}]
}
 * idmu-rest1 Environment Variable 
{"host":"localhost","port":"80","url":"idmuTest/{path}","parseAs":"none"}
*/

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	AbstractProviderTest.class,
	CacheProviderTest.class,
	CloudantProviderTest.class,
	FileSystemProviderTest.class,
	JdbcProviderTest.class, 
	MongoProviderTest.class,
	RestProviderTest.class, 
	StubProviderTest.class })
public class AllTests {

}
