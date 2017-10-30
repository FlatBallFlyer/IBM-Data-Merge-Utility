package com.ibm.util.merge.template.directive.enrich.source;
/*
 * SPECIAL TESTING REQUIREMENTS
 * Environment Variable VCAP_SERVICES
{"idmu-mongo1":[{"credentials":{"username": "user","password": "password","host": "localhost","port": "80","url": "connectstring"}}],
 "idmu-cloud1":[{"credentials":{"username": "user","password": "password","host": "localhost","port": "80","url": ""}}],
 "compose-for-mysql":[{"credentials":{}}]
 }

 * ENVIRONMENT VARIABLE idmu-rest1 Environment Variable 
{"host":"localhost","port":"80","url":"idmuTest/{path}","parseAs":"none"}
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	AbstractSourceTest.class,
	CacheSourceTest.class,
	CloudantSourceTest.class,
	FileSystemSourceTest.class,
	JdbcSourceTest.class, 
	MongoSourceTest.class,
	RestSourceTest.class,
	StubSourceTest.class,
	SourceListTest.class
})
public class AllTests {

}
