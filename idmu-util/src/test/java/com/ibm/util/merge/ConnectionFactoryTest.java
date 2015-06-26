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

import static org.junit.Assert.*;
import java.sql.Connection;
import org.junit.Test;


public class ConnectionFactoryTest {

	@Test
	public void testGetDataConnection() throws MergeException {
		Connection con = ConnectionFactory.getDataConnection("testgenDB", "TestGuid");
		assertNotNull(con);
		assertEquals(1, ConnectionFactory.size());
		con = ConnectionFactory.getDataConnection("testgenDB", "TestGuid");
		assertNotNull(con);
		assertEquals(1, ConnectionFactory.size());
	}

	@Test
	public void testCloseDataConnection() {
		ConnectionFactory.close("TestGuid");
		assertEquals(0, ConnectionFactory.size());
	}

}