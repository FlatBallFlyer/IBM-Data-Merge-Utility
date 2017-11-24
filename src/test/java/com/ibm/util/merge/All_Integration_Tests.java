package com.ibm.util.merge;

import java.io.File;
import org.junit.Test;

public class All_Integration_Tests {
	//////////////////////////////////////////////////////////////////////////////////
	// In order to run these integration tests you need to run the docker testing container
	// which runs all the required test databases. If you have not already done so
	// install Docker - https://www.docker.com and then run the following command
	// docker run -p 3306:3306 --name idmu-mysql -d flatballflyer/idmu-test-mysql:latest
	// 
	// NOTE: If you change the root password you will need to change the corresponding Config Environment variables for the JDBC data source.
	// TODO: This is just a MySql container, will implement as a full stack with PWD's hidden
	//////////////////////////////////////////////////////////////////////////////////
	private final MergeTestHarness harness = new MergeTestHarness();

	@Test
	public void MySql1() throws Throwable {
		// Given Customer ID as a parameter - create Contacts Report with Corporate Info
		harness.testTemplates(new File("src/test/resources/integration/mysql1"));
	}

}