package com.ibm.util.merge.template.directive.enrich.provider;

import java.util.HashMap;

import com.google.gson.JsonElement;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.parser.DataProxyJson;
import com.ibm.util.merge.data.parser.Parser;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;

public abstract class AbstractProvider {
	public static final int PROVIDER_CACHE 		= 1;
	public static final int PROVIDER_CLOUDANT 	= 2;
	public static final int PROVIDER_FILE	 	= 3;
	public static final int PROVIDER_JDBC 		= 4;
	public static final int PROVIDER_MONGO 		= 5;
	public static final int PROVIDER_REST 		= 6;
	public static final int PROVIDER_STUB 		= 7;
	public static final HashMap<Integer, String> PROVIDER_TYPES() {
		HashMap<Integer, String> values = new HashMap<Integer, String>();
		values.put(PROVIDER_CACHE, 		"Template Cache");
		values.put(PROVIDER_CLOUDANT, 	"Cloudant");
		values.put(PROVIDER_FILE, 		"File System");
		values.put(PROVIDER_JDBC, 		"JDBC - JNDI");
		values.put(PROVIDER_MONGO, 		"Mongo");
		values.put(PROVIDER_REST, 		"Rest");
		values.put(PROVIDER_STUB, 		"Stub");
		return values;
	}
	
	private int type;
	private AbstractSource source;
	protected DataProxyJson proxy = new DataProxyJson();
	protected Parser parser = new Parser();

	public AbstractProvider(AbstractSource source) throws MergeException {
		this.type = PROVIDER_STUB;
		this.source = source;
	}
	
	public abstract DataElement get(Template template) throws MergeException;
	public abstract void put(Template template) throws MergeException;
	public abstract void post(Template template) throws MergeException;
	public abstract void delete(Template template) throws MergeException;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if (PROVIDER_TYPES().containsKey(type)) {
			this.type = type;
		}
	}

	public AbstractSource getSource() {
		return source;
	}

	public String getEnvironmentString() throws MergeException {
		String configString;
		if (this.source.getEnv().startsWith("VCAP:")) {
			configString = getVcapEntry();
		} else {
			configString = System.getenv(this.source.getEnv());
		}
		if (null == configString) {
			throw new Merge500("Enviornment Variable for Enrichment Source Credentials not found:" + this.source.getName() + ":" + this.source.getEnv());
		}
		return configString;
	}
	
	public String getVcapEntry() throws MergeException {
		String serviceName = this.source.getEnv().replace("VCAP:","");
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		if (null == VCAP_SERVICES) {
			throw new Merge500("VCAP_SERVICES enviornment variable missing");
		}
		JsonElement vcap = proxy.fromJSON(VCAP_SERVICES, JsonElement.class);
		if (!vcap.isJsonObject()) {
			throw new Merge500("Malformed VCAP Object:" + VCAP_SERVICES);
		}
		if (!vcap.getAsJsonObject().has(serviceName)) {
			throw new Merge500("VCAP Service Not Found:" + serviceName + ":" + VCAP_SERVICES);
		}
		if (!vcap.getAsJsonObject().get(serviceName).isJsonArray()) {
			throw new Merge500("Malformed VCAP Services Object:" + serviceName + ":" + VCAP_SERVICES);
		}
		if (vcap.getAsJsonObject().get(serviceName).getAsJsonArray().size() < 1 ) {
			throw new Merge500("Missing VCAP Services Object:" + serviceName + ":" + VCAP_SERVICES);
		}
		return proxy.toJson(vcap.getAsJsonObject().get(serviceName).getAsJsonArray().get(0));
	}
	
	public String getJsonMemeber(JsonElement element, String member) throws MergeException {
		if (element.isJsonObject()) {
			if (element.getAsJsonObject().has(member)) {
				if (element.getAsJsonObject().get(member).isJsonPrimitive()) {
					return element.getAsJsonObject().get(member).getAsJsonPrimitive().getAsString();
				}
			}
		}
		throw new Merge500("Missing Json Object / Member:" + member + " in:" + proxy.toJson(element));
	}
	
}
