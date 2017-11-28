package com.ibm.util.merge;

import java.io.File;
import org.junit.Test;

public class All_Integration_Tests {
	//////////////////////////////////////////////////////////////////////////////////
	// In order to run these integration tests you need to run the docker testing container
	// which runs all the required test databases. If you have not already done so
	// install Docker - https://www.docker.com and then run the following commands to start test containers
	// TODO: Re-factor to docker stack
	// docker run -p 3306:3306 --name idmu-mysql -d flatballflyer/idmu-test-mysql:latest
	// docker run -p 27017:27017 --name idmu-mongo -d flatballflyer/idmu-test-mongo:latest
	// docker run -p 50000:50000 --name idmu-db2 -v $(pwd):/share -d flatballflyer/idmu-test-db2:latest
	// docker run -p 8383:80 --name idmu-rest -d flatballflyer/idmu-test-rest:latest
	// 
	//////////////////////////////////////////////////////////////////////////////////
	private final MergeTestHarness harness = new MergeTestHarness();

	@Test
	public void Rest1() throws Throwable {
		// Given Customer ID as a parameter - create Contacts Report with Corporate Info
		harness.testTemplates(new File("src/test/resources/integration/restTest"));
	}

	@Test
	public void MySql1() throws Throwable {
		// Given Customer ID as a parameter - create Contacts Report with Corporate Info
		harness.testTemplates(new File("src/test/resources/integration/mysql1"));
	}

	@Test
	public void MyDb2() throws Throwable {
		// Given Customer ID as a parameter - create Contacts Report with Corporate Info
		harness.testTemplates(new File("src/test/resources/integration/db2Express"));
	}

	@Test
	public void SqlToMongo() throws Throwable {
		// Transform the JDBC MySql database to Mongo Insert commands
		harness.testTemplates(new File("src/test/resources/integration/mysqlToMongo"));
	}

	@Test
	public void Mongo1() throws Throwable {
		// Given Customer ID as a parameter - create Contacts Report with Corporate Info
		harness.testTemplates(new File("src/test/resources/integration/mongo1"));
	}

}