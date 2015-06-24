package com.ibm.util.merge.directive;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	InsertSubsCsvTest.class, 
	InsertSubsHtmlTest.class,
	InsertSubsSqlTest.class, 
	InsertSubsTagTest.class,
	ReplaceColCsvTest.class, 
	ReplaceColHtmlTest.class,
	ReplaceColSqlTest.class, 
	ReplaceMarkupHtmlTest.class,
	ReplaceRowCsvTest.class, 
	ReplaceRowHtmlTest.class,
	ReplaceRowSqlTest.class, 
	ReplaceValueTest.class,
	RequireTest.class })
public class AllTests {

}
