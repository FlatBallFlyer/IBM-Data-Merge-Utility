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

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.ParseData;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class RestProvider extends AbstractProvider {
	private final String username;
	private final String password;
	private final String host;
	private final String port;
	private final String url;
	
	public RestProvider(String source, String dbName, Merger context) throws MergeException {
		super(source, dbName, context);

		// Get Credentials
		String config = context.getConfig().getEnvironmentString(source);
		try {
			DataObject credentials = parser.parse(ParseData.PARSE_JSON, config).getAsObject().get("credentials").getAsObject();
			this.username = credentials.get("username").getAsPrimitive();
			this.password = credentials.get("password").getAsPrimitive();
			this.host = 	credentials.get("uri_cli").getAsPrimitive();
			this.port = 	credentials.get("ca_certificate_base64").getAsPrimitive();
			this.url = 		credentials.get("uri").getAsPrimitive();
		} catch (MergeException e) {
			throw new Merge500("Invalid HTML Rest Provider for:" + source + "value: " + config);
		}
	}

	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws Merge500 {
		Content query = new Content(wrapper, enrichCommand, TagSegment.ENCODE_NONE);
		query.replace(replace, false, context.getConfig().getNestLimit());
		String theUrl = "";
		String fetchedData = "";

		try {
			// TODO- Basic HTML Authentication (username password)
			theUrl = "http://" + this.getHost() + ":" + this.getPort() + "/" + query.getValue();
			fetchedData = IOUtils.toString(
					new BufferedReader(
					new InputStreamReader(
					new URL(theUrl).openStream())));
		} catch (MalformedURLException e) {
			throw new Merge500("Malformed URL:" + theUrl);
		} catch (IOException e) {
			throw new Merge500("I-O Exception at:" + theUrl);
		}

		return new DataPrimitive(fetchedData);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getUrl() {
		return url;
	}

}
