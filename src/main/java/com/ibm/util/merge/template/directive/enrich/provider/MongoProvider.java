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
import com.ibm.util.merge.template.directive.ParseData;

public class MongoProvider implements ProviderInterface {
	private final String source;
	private final String dbName;
	private transient final Merger context;
	private transient final Parser parser;
	private transient final String connection; // TODO Mongo Connection type
	
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
			DataObject credentials = parser.parse(ParseData.PARSE_JSON, config).getAsObject().get("credentials").getAsObject();
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
