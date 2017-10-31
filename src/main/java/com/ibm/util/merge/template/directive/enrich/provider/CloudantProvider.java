package com.ibm.util.merge.template.directive.enrich.provider;
/*
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

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;
import com.google.gson.JsonElement;
import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.enrich.source.CloudantClientMgr;
import com.ibm.util.merge.template.directive.enrich.source.CloudantSource;

public class CloudantProvider extends AbstractProvider {
	private CloudantClient cloudant = null;
	private Database db = null;
	
	public CloudantProvider(String source, String dbName, Merger context) throws MergeException {
		super(source, dbName, context);
		String configString = "";
		String username;
		String password;
		String host;
		String port;
		String url;
		
		// Fetch Credentials
		try {
			configString = context.getConfig().getEnvironmentString(source);
			DataObject credentials = proxy.fromJSON(configString, DataElement.class).getAsList().get(0).getAsObject().get("credentials").getAsObject();
			username = 	credentials.get("username").getAsPrimitive();
			password = 	credentials.get("password").getAsPrimitive();
			host = 		credentials.get("host").getAsPrimitive();
			port = 		credentials.get("port").getAsPrimitive();
			url = 		credentials.get("url").getAsPrimitive();
		} catch (MergeException e){
			throw new Merge500("Malformed or Missing Cloudant Source Credentials found for:" + source + " for " + configString);
		}
		
		// Get the connection
		synchronized (CloudantClientMgr.class) {
			try {
				this.cloudant = ClientBuilder
						.account(username)
						.username(username)
						.password(password)
						.build();
			} catch (CouchDbException e) {
				throw new Merge500("Unable to connect to Cloudant repository" + e.getMessage());
			}
		} // end synchronized
		
		// Get the database object
		db = cloudant.database(this.getDbName(), true);

	}
	

	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) {
		String result = "";  // TODO - Make Cloudant Call
		return new DataPrimitive(result);
	}
}
