/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.util.merge;

import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ConnectionFactoryTest {
	private ConnectionFactory cf;

	@Test
	public void testGetDataConnection() throws Exception{
		cf = new ConnectionFactory();
		Connection con = cf.lookupDataSource("testgenDB").getConnection();//getDataConnection("testgenDB", "TestGuid");
		assertNotNull(con);
		con.close();
//		assertEquals(1, cf.size());
//		con = cf.getDataConnection("testgenDB", "TestGuid");
//		assertNotNull(con);
//		assertEquals(1, cf.size());
	}

//	@Test
//	public void testCloseDataConnection() {
//		cf = new ConnectionFactory();
//		cf.releaseConnection("TestGuid");
//		assertEquals(0, cf.size());
//	}

}
