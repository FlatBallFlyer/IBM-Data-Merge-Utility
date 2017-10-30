package com.ibm.util.merge.template.content;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	BookmarkSegmentTest.class, 
	ContentTest.class,
	SegmentTest.class, 
	TagSegmentTest.class, 
	TextSegmentTest.class })
public class AllTests {

}
