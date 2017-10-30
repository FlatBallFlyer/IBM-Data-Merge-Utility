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

import java.io.BufferedReader;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.ibm.util.merge.Config;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.ParseData;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.RestSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class RestProvider extends AbstractProvider {
	private Credentials credentials;
	private class Credentials { // Assumed to be like this - need to see VCAP_SERVICES for Mongo Service
		protected String username;
		protected String password;
		protected String host;
		protected String port;
		protected String url;
	}
	
	public RestProvider(AbstractSource source) throws MergeException {
		super(source);
		this.setType(AbstractProvider.PROVIDER_REST);
		credentials = new Credentials();
	}

	@Override
	public DataElement get(Template template) throws MergeException {
		fetchCredentials();
		String fetchedData = "";
		String theUrl = "empty";
		try {
			// TODO- Basic HTML Authentication (username password)
			Content url = new Content(template.getWrapper(), this.getSource().getGetCommand(), TagSegment.ENCODE_HTML);
			url.replace(template.getReplaceStack(), false, Config.MAX_NEST);
			theUrl = "http://" + this.getHost() + ":" + this.getPort() + "/" + theUrl;
			fetchedData = IOUtils.toString(
					new BufferedReader(
					new InputStreamReader(
					new URL(theUrl).openStream())));
		} catch (MalformedURLException e) {
			throw new Merge500("Malformed URL:" + theUrl);
		} catch (IOException e) {
			throw new Merge500("I-O Exception at:" + theUrl);
		}

		RestSource source = (RestSource) this.getSource();
		if (source.getParseAs() == ParseData.PARSE_NONE) {
			return new DataPrimitive(fetchedData);
		} else {
			return parser.parse(source.getParseAs(), fetchedData);
		}
	}

	@Override
	public void put(Template template) throws MergeException {
		// TODO HTTP Put
		
	}

	@Override
	public void post(Template template) throws MergeException {
		// TODO HTTP Post
		
	}

	@Override
	public void delete(Template template) throws MergeException {
		// TODO HTTP Delete
	}

	private void fetchCredentials() throws MergeException {
		String configString = this.getEnvironmentString();
		JsonElement newConfig = this.proxy.fromJSON(configString, JsonElement.class);
		if (null == newConfig) {
			throw new Merge500("Malformed Rest Source Credentials found at:" + this.getSource().getEnv() + ":" + configString);
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

}
