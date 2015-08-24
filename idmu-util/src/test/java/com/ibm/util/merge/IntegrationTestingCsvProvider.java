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

import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.db.ConnectionPoolManager;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.TemplatePersistence;
import com.ibm.util.merge.persistence.FilesystemPersistence;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationTestingCsvProvider {
	static final String csvCorporate = "IDCORPORATE,FROM_VALUE,TO_VALUE\n1,corpUrl,www.spacely.com\n2,corpStreet,101 Future Ave.\n3,corpCity,Space City\n4,corpState,IS\n5,corpZip,99353";
	static final String csvCustomer  = "IDCUSTOMER,PRIMARY,NAME,REVENUE,PROFIT,STREET,CITY,STATE,ZIP,PHONE\n1,James,General Motors,9824,806,5791 Pleasant Prairie End,Dysart,PA,16188-0761,(878) 179-6603";
	static final String csvCustomers = "IDCUSTOMER,PRIMARY,NAME,REVENUE,PROFIT,STREET,CITY,STATE,ZIP,PHONE\n1,James,General Motors,9824,806,5791 Pleasant Prairie End,Dysart,PA,16188-0761,(878) 179-6603\n2,Robert,Exxon Mobil,5661,585,1309 Burning Trail,Yazoo City,NE,68970-0108,(531) 984-8463";
	static final String csvContacts1	= "IDCONTACT,IDCUSTOMER,NAME,PREFERENCE,EMAIL,PHONE\n1,1,James,paper,James@General.com,(878) 555-0211\n21,1,Daniel,SMS,,(878) 555-2221\n41,1,Alan,email,Alan@General.com,\n61,1,Harry,paper,Harry@General.com,(878) 555-6261\n81,1,Ernest,SMS,Ernest@General.com,(878) 555-8281\n101,1,Linda,email,Linda@General.com,(878) 555-0211\n121,1,Judith,paper,Judith@General.com,(878) 555-2211\n141,1,Peggy,SMS,Peggy@General.com,(878) 555-4211\n161,1,Vicki,email,Vicki@General.com,(878) 555-6211\n181,1,Teresa,paper,Teresa@General.com,(878) 555-8211";
	static final String csvContacts2	= "IDCONTACT,IDCUSTOMER,NAME,PREFERENCE,EMAIL,PHONE\n2,2,Robert,email,Robert@Exxon.com,(531) 555-0422\n22,2,Edward,paper,Edward@Exxon.com,(531) 555-2422\n42,2,Willie,SMS,,(531) 555-4442\n62,2,Samuel,email,Samuel@Exxon.com,(531) 555-6462\n82,2,Scott,paper,Scott@Exxon.com,(531) 555-8482\n102,2,Mary,SMS,Mary@Exxon.com,(531) 555-0412\n122,2,Janice,email,Janice@Exxon.com,(531) 555-2412\n142,2,Gail,paper,Gail@Exxon.com,(531) 555-4412\n162,2,Sherry,SMS,Sherry@Exxon.com,(531) 555-6412\n182,2,Denise,email,Denise@Exxon.com,";
	static final String csvContacts3	= "IDCONTACT,IDCUSTOMER,NAME,PREFERENCE,EMAIL,PHONE\n3,3,John,SMS,,(417) 555-0633\n23,3,Mark,email,Mark@USSteal.com,(417) 555-2623\n43,3,Jeffrey,paper,Jeffrey@USSteal.com,(417) 555-4643\n63,3,Howard,SMS,Howard@USSteal.com,(417) 555-6663\n83,3,Fred,email,Fred@USSteal.com,\n103,3,Patricia,paper,Patricia@USSteal.com,(417) 555-0613\n123,3,Cynthia,SMS,Cynthia@USSteal.com,(417) 555-2613\n143,3,Virginia,email,Virginia@USSteal.com,(417) 555-4613\n163,3,Laura,paper,Laura@USSteal.com,(417) 555-6613\n183,3,Lois,SMS,Lois@USSteal.com,(417) 555-8613";
	HashMap<String, String[]> parameterMap;
	File templateDir 	= new File("src/test/resources/templates/");
	File outputDir 		= new File("src/test/resources/testout/");
	File validateDir 	= new File("src/test/resources/valid/");
    JsonProxy jsonProxy = new PrettyJsonProxy();
    TemplatePersistence persist = new FilesystemPersistence(templateDir, jsonProxy);
    ConnectionPoolManager manager = new ConnectionPoolManager();
	Properties props = new Properties();
    TemplateFactory tf;

	@Before
	public void setup() throws MergeException, IOException {
		tf = new TemplateFactory(props);
		// Initialize requestMap (usually from request.getParameterMap())
		parameterMap = new HashMap<>();
		parameterMap.put("csvCorporate", new String[]{csvCorporate});
		parameterMap.put("csvCustomer", new String[]{csvCustomer});
		parameterMap.put("csvCustomers",new String[]{csvCustomers});
		parameterMap.put("csvContact1", new String[]{csvContacts1});
		parameterMap.put("csvContact2", new String[]{csvContacts2});
		parameterMap.put("csvContact3", new String[]{csvContacts3});
	}

	@After
	public void teardown() throws IOException {
		FileUtils.cleanDirectory(outputDir);
	}
	
	@Test
	public void testDefaultTemplate() throws MergeException, IOException {
		String output = tf.getMergeOutput(parameterMap);
		assertEquals("This is the Default Template", output);
	}

	@Test
	public void testCsvDefaultDataTar() throws Exception {
		testIt("csvDef.functional.", "tar");
	}

	@Test
	public void testCsvDefaultDataZip() throws Exception {
		testIt("csvDef.functional.", "zip");
	}

	@Test
	public void testCsvTagDataTar() throws Exception {
		testIt("csvTag.functional.","tar");
	}

	@Test
	public void testCsvTagDataZip() throws Exception {
		testIt("csvTag.functional.", "zip");
	}

	@Test
	public void testCsvUrlDataTar() throws Exception {
		testIt("csvUrl.functional.","tar");
	}

	@Test
	public void testCsvUrlDataZip() throws Exception {
		testIt("csvUrl.functional.","zip");
	}

	private void testIt(String fullName, String type) throws Exception {
		String fileName = fullName + type;
		parameterMap.put("DragonFlyFullName", 	new String[]{fullName});
		parameterMap.put("DragonFlyOutputFile", new String[]{fileName});
		parameterMap.put("DragonFlyOutputType", new String[]{type});
		String output = tf.getMergeOutput(parameterMap);
		assertTrue(output.trim().isEmpty());
//		CompareArchives.assertArchiveEquals(type, new File(validateDir, fileName).getAbsolutePath(), new File(outputDir, fileName).getAbsolutePath());
	}

}
