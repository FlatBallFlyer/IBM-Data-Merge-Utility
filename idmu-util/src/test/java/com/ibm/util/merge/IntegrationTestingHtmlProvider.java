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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class IntegrationTestingHtmlProvider {
	HashMap<String, String[]> parameterMap;
	String templateDir 	 = "src/test/resources/templates/";
	String outputDir 	 = "src/test/resources/testout/"; 
	String validateDir 	 = "src/test/resources/valid/";
	String htmlCorporate = "<html><head></head><body><table><tr><th>IDCORPORATE</th><th>FROM_VALUE</th><th>TO_VALUE</th></tr><tr><td>1</td><td>corpUrl</td><td>www.spacely.com</td></tr><tr><td>2</td><td>corpStreet</td><td>101 Future Ave.</td></tr><tr><td>3</td><td>corpCity</td><td>Space City</td></tr><tr><td>4</td><td>corpState</td><td>IS</td></tr><tr><td>5</td><td>corpZip</td><td>99353</table></body></html>";
	String htmlCustomer  = "<html><head></head><body><table><tr><th>IDCUSTOMER</th><th>PRIMARY</th><th>NAME</th><th>REVENUE</th><th>PROFIT</th><th>STREET</th><th>CITY</th><th>STATE</th><th>ZIP</th><th>PHONE</th></tr><tr><td>1</td><td>James</td><td>General Motors</td><td>9824</td><td>806</td><td>5791 Pleasant Prairie End</td><td>Dysart</td><td>PA</td><td>16188-0761</td><td>(878) 179-6603</td></tr></table></body></html>";
	String htmlCustomers = "<html><head></head><body><table><tr><th>IDCUSTOMER</th><th>PRIMARY</th><th>NAME</th><th>REVENUE</th><th>PROFIT</th><th>STREET</th><th>CITY</th><th>STATE</th><th>ZIP</th><th>PHONE</th></tr><tr><td>1</td><td>James</td><td>General Motors</td><td>9824</td><td>806</td><td>5791 Pleasant Prairie End</td><td>Dysart</td><td>PA</td><td>16188-0761</td><td>(878) 179-6603</td></tr><tr><td>2</td><td>Robert</td><td>Exxon Mobil</td><td>5661</td><td>585</td><td>1309 Burning Trail</td><td>Yazoo City</td><td>NE</td><td>68970-0108</td><td>(531) 984-8463</td></tr><tr><td>3</td><td>John</td><td>U.S. Steel</td><td>3250</td><td>195</td><td>3930 Iron Walk</td><td>Wild Horse</td><td>MO</td><td>65453-3393</td><td>(417) 591-1188</td></tr></table></body></html>";
	String htmlContacts1 = "<html><head></head><body><table><tr><th>IDCONTACT</th><th>IDCUSTOMER</th><th>NAME</th><th>PREFERENCE</th><th>EMAIL</th><th>PHONE</th></tr><tr><td>1</td><td>1</td><td>James</td><td>paper</td><td>James@General.com</td><td>(878) 555-0211</td></tr><tr><td>21</td><td>1</td><td>Daniel</td><td>SMS</td><td></td><td>(878) 555-2221</td></tr><tr><td>41</td><td>1</td><td>Alan</td><td>email</td><td>Alan@General.com</td><td></td></tr><tr><td>61</td><td>1</td><td>Harry</td><td>paper</td><td>Harry@General.com</td><td>(878) 555-6261</td></tr><tr><td>81</td><td>1</td><td>Ernest</td><td>SMS</td><td>Ernest@General.com</td><td>(878) 555-8281</td></tr><tr><td>101</td><td>1</td><td>Linda</td><td>email</td><td>Linda@General.com</td><td>(878) 555-0211</td></tr><tr><td>121</td><td>1</td><td>Judith</td><td>paper</td><td>Judith@General.com</td><td>(878) 555-2211</td></tr><tr><td>141</td><td>1</td><td>Peggy</td><td>SMS</td><td>Peggy@General.com</td><td>(878) 555-4211</td></tr><tr><td>161</td><td>1</td><td>Vicki</td><td>email</td><td>Vicki@General.com</td><td>(878) 555-6211</td></tr><tr><td>181</td><td>1</td><td>Teresa</td><td>paper</td><td>Teresa@General.com</td><td>(878) 555-8211</td></tr></table></body></html>";
	String htmlContacts2 = "<html><head></head><body><table><tr><th>IDCONTACT</th><th>IDCUSTOMER</th><th>NAME</th><th>PREFERENCE</th><th>EMAIL</th><th>PHONE</th></tr><tr><td>2</td><td>2</td><td>Robert</td><td>email</td><td>Robert@Exxon.com</td><td>(531) 555-0422</td></tr><tr><td>22</td><td>2</td><td>Edward</td><td>paper</td><td>Edward@Exxon.com</td><td>(531) 555-2422</td></tr><tr><td>42</td><td>2</td><td>Willie</td><td>SMS</td><td></td><td>(531) 555-4442</td></tr><tr><td>62</td><td>2</td><td>Samuel</td><td>email</td><td>Samuel@Exxon.com</td><td>(531) 555-6462</td></tr><tr><td>82</td><td>2</td><td>Scott</td><td>paper</td><td>Scott@Exxon.com</td><td>(531) 555-8482</td></tr><tr><td>102</td><td>2</td><td>Mary</td><td>SMS</td><td>Mary@Exxon.com</td><td>(531) 555-0412</td></tr><tr><td>122</td><td>2</td><td>Janice</td><td>email</td><td>Janice@Exxon.com</td><td>(531) 555-2412</td></tr><tr><td>142</td><td>2</td><td>Gail</td><td>paper</td><td>Gail@Exxon.com</td><td>(531) 555-4412</td></tr><tr><td>162</td><td>2</td><td>Sherry</td><td>SMS</td><td>Sherry@Exxon.com</td><td>(531) 555-6412</td></tr><tr><td>182</td><td>2</td><td>Denise</td><td>email</td><td>Denise@Exxon.com</td><td>(531) 555-8412</td></tr></table></body></html>";
	String htmlContacts3 = "<html><head></head><body><table><tr><th>IDCONTACT</th><th>IDCUSTOMER</th><th>NAME</th><th>PREFERENCE</th><th>EMAIL</th><th>PHONE</th></tr><tr><td>3</td><td>3</td><td>John</td><td>SMS</td><td></td><td>(417) 555-0633</td></tr><tr><td>23</td><td>3</td><td>Mark</td><td>email</td><td>Mark@USSteal.com</td><td>(417) 555-2623</td></tr><tr><td>43</td><td>3</td><td>Jeffrey</td><td>paper</td><td>Jeffrey@USSteal.com</td><td>(417) 555-4643</td></tr><tr><td>63</td><td>3</td><td>Howard</td><td>SMS</td><td>Howard@USSteal.com</td><td>(417) 555-6663</td></tr><tr><td>83</td><td>3</td><td>Fred</td><td>email</td><td>Fred@USSteal.com</td><td>(417) 555-8683</td></tr><tr><td>103</td><td>3</td><td>Patricia</td><td>paper</td><td>Patricia@USSteal.com</td><td>(417) 555-0613</td></tr><tr><td>123</td><td>3</td><td>Cynthia</td><td>SMS</td><td>Cynthia@USSteal.com</td><td>(417) 555-2613</td></tr><tr><td>143</td><td>3</td><td>Virginia</td><td>email</td><td>Virginia@USSteal.com</td><td>(417) 555-4613</td></tr><tr><td>163</td><td>3</td><td>Laura</td><td>paper</td><td>Laura@USSteal.com</td><td>(417) 555-6613</td></tr><tr><td>183</td><td>3</td><td>Lois</td><td>SMS</td><td>Lois@USSteal.com</td><td>(417) 555-8613</td></tr></table></body></html>";
	private TemplateFactory tf;
	private ZipFactory zf;
	private ConnectionFactory cf;

	@Before
	public void setup() throws MergeException, IOException {
		tf = new TemplateFactory();
		zf = new ZipFactory();
		cf = new ConnectionFactory();
		// Initialize Factories
		tf.reset();
		tf.setDbPersistance(false);
		tf.setTemplateFolder(templateDir);
		tf.loadAll();
		zf.setOutputroot(outputDir);
		
		// Initialize requestMap (usually from request.getParameterMap())
		parameterMap = new HashMap<String,String[]>();
		parameterMap.put("htmlCorporate", new String[]{htmlCorporate});
		parameterMap.put("htmlCustomer",  new String[]{htmlCustomer});
		parameterMap.put("htmlCustomers", new String[]{htmlCustomers});
		parameterMap.put("htmlContact1",  new String[]{htmlContacts1});
		parameterMap.put("htmlContact2",  new String[]{htmlContacts2});
		parameterMap.put("htmlContact3",  new String[]{htmlContacts2});
	 	
		// Reset the output directory
//		FileUtils.cleanDirectory(new File(outputDir)); 
	}

	@After
	public void teardown() throws IOException {
//		FileUtils.cleanDirectory(new File(outputDir)); 
	}
	
	@Test
	public void testDefaultTemplate() throws MergeException, IOException {
		Template root = tf.getTemplate(parameterMap);
		String output = root.merge(zf, tf, cf);
		root.packageOutput(zf, cf);
		assertEquals(String.join("\n", Files.readAllLines(Paths.get(validateDir + "merge1.output"))), output);
	}

	@Test
	public void testhtmlDefaultDataTar() throws MergeException, IOException, NoSuchAlgorithmException {
		testIt("htmlDef.functional.", "tar");
	}

	@Test
	public void testhtmlDefaultDataZip() throws MergeException, IOException, NoSuchAlgorithmException {
		testIt("htmlDef.functional.","zip");
	}

	@Test
	public void testhtmlTagDataTar() throws MergeException, IOException, NoSuchAlgorithmException {
		testIt("htmlTag.functional.","tar");
	}

	@Test
	public void testhtmlTagDataZip() throws MergeException, IOException, NoSuchAlgorithmException {
		testIt("htmlTag.functional.", "zip");
	}

	@Test
	public void testhtmlUrlDataTar() throws MergeException, IOException, NoSuchAlgorithmException {
		testIt("htmlUrl.functional.","tar");
	}

	@Test
	public void testhtmlUrlDataZip() throws MergeException, IOException, NoSuchAlgorithmException {
		testIt("htmlUrl.functional.","zip");
	}

	/**
	 * @param fullName
	 * @param type
	 * @throws MergeException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	private String testIt(String fullName, String type) throws MergeException, IOException, NoSuchAlgorithmException {
		parameterMap.put("DragonOutputType", 	new String[]{type});
		parameterMap.put("DragonFlyOutputFile", new String[]{fullName+type});
		parameterMap.put("DragonFlyFullName", 	new String[]{fullName});
		Template root = tf.getTemplate(parameterMap);
		String output = root.merge(zf, tf, cf);
		root.packageOutput(zf, cf);
		CompareArchives.assertArchiveEquals(type, validateDir + fullName + type, outputDir + fullName + type);
		return output;
	}
}
