/*
 * 
 * Copyright 2015-2017 IBM
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
package com.ibm.util.merge.template.directive.enrich.provider;
/*
"mongoDb": [{
	"credentials": {
		"username": "some-user-name",
		"password": "some-password"
    }
}]

"SOURCE_NAME": [
        {
            "credentials": {
                "username": "",
                "password": "",
                "host": "",
                "port": ,
                "url": ""
            }
        }
    ],
*/

import java.util.HashMap;

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;

/**
 * Implements MongoDb access
 * 
 * @author flatballflyer
 *
 */
public class MongoProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Merger context;
	private transient final Parser parser;
	private transient final String connection; // TODO Mongo Connection type
	
	/**
	 * Instantiate the provider and get the db connection
	 * 
	 * @param source
	 * @param dbName
	 * @param context
	 * @throws MergeException
	 */
	public MongoProvider(String source, String dbName, Merger context) throws MergeException {
		this.source = source;
		this.dbName = dbName;
		this.context = context;
		this.parser = new Parser();
		
		String db_type;
		String name;
		String uri_cli;
		String ca_cert;
		String deployment_id;
		String uri;

		// Get Credentials (TODO Assumed same as JDBC)
		String config = context.getConfig().getEnv(source);
		try {
			DataObject credentials = parser.parse(Parser.PARSE_JSON, config).getAsObject().get("credentials").getAsObject();
			db_type = 		credentials.get("db_type").getAsPrimitive();
			name = 			credentials.get("name").getAsPrimitive();
			uri_cli = 		credentials.get("uri_cli").getAsPrimitive();
			ca_cert = 		credentials.get("ca_certificate_base64").getAsPrimitive();
			deployment_id = credentials.get("deployment_id").getAsPrimitive();
			uri = 			credentials.get("uri").getAsPrimitive();
		} catch (MergeException e) {
			throw new Merge500("Invalid JDBC Provider for:" + source + "value: " + config);
		}

		// TODO - Get Connection
		this.connection = db_type + name + uri_cli + ca_cert + deployment_id + uri;
		
	}

	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws MergeException {
		Content query = new Content(wrapper, enrichCommand, TagSegment.ENCODE_SQL);
		query.replace(replace, false, context.getConfig().getNestLimit());
		
		String result = connection;  // TODO - Use connection to make Mongo Call
		return new DataPrimitive(result);
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public String getDbName() {
		return this.dbName;
	}

	@Override
	public Merger getContext() {
		return this.context;
	}
}
