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

import com.google.gson.JsonElement;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.MongoSource;

public class MongoProvider extends AbstractProvider {
	private class Credentials { // Assumed to be like this - need to see VCAP_SERVICES for Mongo Service
		protected String username;
		protected String password;
		protected String host;
		protected String port;
		protected String url;
	}
	private Credentials credentials;
	private String connection; // TODO Mongo Connection Object
	
	public MongoProvider(AbstractSource source) throws MergeException {
		super(source);
		this.setType(AbstractProvider.PROVIDER_MONGO);
	}

	@Override
	public DataElement get(Template template) throws MergeException {
		MongoSource source = (MongoSource) this.getSource();
		Content query = new Content(template.getWrapper(), source.getGetCommand(), TagSegment.ENCODE_JSON);
		query.replace(template.getReplaceStack(), false, Config.MAX_NEST);
		execute(query.getValue());
		return null;
	}

	private void execute(String query) throws MergeException {
		getConnection();
		// TODO Get Connection, Execute Mongo Query
	}

	private String getConnection() throws MergeException {
		if (null == this.connection) {
			fetchCredentials();
			this.connection = "";// TODO Connect to Mongo 
		}
		return this.connection;
	}
		
	private void fetchCredentials() throws MergeException {
		String configString = this.getEnvironmentString();
		JsonElement newConfig = this.proxy.fromJSON(configString, JsonElement.class);
		if (null == newConfig) {
			throw new Merge500("Malformed Mongo Source Credentials found at:" + this.getSource().getEnv() + ":" + configString);
		}
		
		if (newConfig.isJsonObject() && newConfig.getAsJsonObject().has("credentials")) {
			JsonElement credentials = newConfig.getAsJsonObject().get("credentials");
			this.setUsername(this.getJsonMemeber(credentials, "username"));
			this.setPassword(this.getJsonMemeber(credentials, "password"));
			this.setHost(this.getJsonMemeber(credentials, "host"));
			this.setPort(this.getJsonMemeber(credentials, "port"));
			this.setUrl(this.getJsonMemeber(credentials, "url"));
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

	@Override
	public void put(Template template) throws MergeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void post(Template template) throws MergeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Template template) throws MergeException {
		// TODO Auto-generated method stub
		
	}

}
