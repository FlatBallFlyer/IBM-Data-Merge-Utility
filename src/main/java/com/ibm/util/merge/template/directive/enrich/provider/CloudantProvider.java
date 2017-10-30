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

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;
import com.google.gson.JsonElement;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.enrich.source.CloudantClientMgr;
import com.ibm.util.merge.template.directive.enrich.source.CloudantSource;

public class CloudantProvider extends AbstractProvider {
	
	private class Credentials {
		protected String username;
		protected String password;
		protected String host;
		protected String port;
		protected String url;
	}

	private transient Credentials credentials;
	private CloudantClient cloudant = null;
	private Database db = null;
	
	public CloudantProvider(CloudantSource source) throws MergeException {
		super(source);
		this.setType(AbstractProvider.PROVIDER_CLOUDANT);
	}

	@Override
	public DataElement get(Template template) throws MergeException {
		String result = "";  // TODO - Make Cloudant Call
		CloudantSource source = (CloudantSource) this.getSource();
		if (source.getParseAs() == ParseData.PARSE_NONE) {
			return new DataPrimitive(result);
		} else {
			return parser.parse(source.getParseAs(), result);
		}
	}
	
	@Override
	public void put(Template template) throws MergeException {
		// Database db = this.getDB();
		// TODO Build and Execute Request - ?db.get??
	}

	@Override
	public void post(Template template) throws MergeException {
		// Database db = this.getDB();
		// TODO Build and Execute Request - ?db.get??
	}

	@Override
	public void delete(Template template) throws MergeException {
		// Database db = this.getDB();
		// TODO Build and Execute Request - ?db.get??
		
	}

	private CloudantClient getClient() throws MergeException {
		if (null == this.cloudant) {
			fetchCredentials();
			synchronized (CloudantClientMgr.class) {
				try {
					CloudantClient client = ClientBuilder.account(this.credentials.username)
							.username(this.credentials.username)
							.password(this.credentials.password)
							.build();
					this.cloudant = client;
				} catch (CouchDbException e) {
					throw new Merge500("Unable to connect to Cloudant repository" + e.getMessage());
				}
			} // end synchronized
			
		}
		return this.cloudant;
	}
	
	public Database getDB() throws MergeException {
		if (db == null) {
			getClient();
			try {
				db = cloudant.database(this.getSource().getDatabaseName(), true);
			} catch (Exception e) {
				throw new Merge500("DB Not found:" + this.getSource().getDatabaseName());
			}
		}
		return db;
	}
	
	public CloudantSource getSource() {
		return (CloudantSource) super.getSource();
	}

	private void fetchCredentials() throws MergeException {
		String configString = this.getEnvironmentString();
		JsonElement newConfig = proxy.fromJSON(configString, JsonElement.class);
		if (null == newConfig) {
			throw new Merge500("Malformed Cloudant Source Credentials found at:" + this.getSource().getEnv());
		}
		if (newConfig.isJsonObject() && newConfig.getAsJsonObject().has("credentials")) {
			JsonElement credentials = newConfig.getAsJsonObject().get("credentials");
			this.setUsername(		this.getJsonMemeber(credentials, "username"));
			this.setPassword(		this.getJsonMemeber(credentials, "password"));
			this.setHost(			this.getJsonMemeber(credentials, "host"));
			this.setPort(			this.getJsonMemeber(credentials, "port"));
			this.setUrl(			this.getJsonMemeber(credentials, "url"));
		} else {
			throw new Merge500("Config Object missing in environment" + this.getSource().getEnv() + ":" + configString);
		}
	}

	public String getUsername() {
		return this.credentials.username;
	}
	public void setUsername(String username) {
		this.credentials.username = username;
	}
	
	public String getPassword() {
		return this.credentials.password;
	}
	public void setPassword(String password) {
		this.credentials.password = password;
	}
	
	public String getHost() {
		return this.credentials.host;
	}
	public void setHost(String host) {
		this.credentials.host = host;
	}
	
	public String getPort() {
		return this.credentials.port;
	}
	public void setPort(String port) {
		this.credentials.port = port;
	}
	
	public String getUrl() {
		return this.credentials.url;
	}
	public void setUrl(String url) {
		this.credentials.url = url;
	}

}
