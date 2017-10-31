package com.ibm.util.merge.template.directive.enrich.provider;
/*
 *   "DATA_SOURCE": [
        {
            "credentials": {
                "db_type": "",
                "name": "",
                "uri_cli": "",
                "ca_certificate_base64": "",
                "deployment_id": "",
                "uri": ""
            },
 */

import com.ibm.util.merge.Merger;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Wrapper;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.ParseData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

public class JdbcProvider extends AbstractProvider {
	private final DataSource jdbcSource;
	private final Connection connection;
	
	public JdbcProvider(String source, String dbName, Merger context) throws MergeException {
		super(source, dbName, context);
		String db_type;
		String name;
		String uri_cli;
		String ca_cert;
		String deployment_id;
		String uri;

		// Get Credentials
		String config = context.getConfig().getEnvironmentString(source);
		try {
			DataObject credentials = parser.parse(ParseData.PARSE_JSON, config).getAsObject().get("credentials").getAsObject();
			db_type = 		credentials.get("db_type").getAsPrimitive();
			name = 			credentials.get("name").getAsPrimitive();
			uri_cli = 		credentials.get("uri_cli").getAsPrimitive();
			ca_cert = 		credentials.get("ca_certificate_base64").getAsPrimitive();
			deployment_id = 	credentials.get("deployment_id").getAsPrimitive();
			uri = 			credentials.get("uri").getAsPrimitive();
		} catch (MergeException e) {
			throw new Merge500("Invalid JDBC Provider for:" + source + "value: " + config);
		}

		// TODO - Get Connection
		try {
			jdbcSource = (DataSource) foo(db_type, name, uri_cli, ca_cert, deployment_id, uri);
			connection = jdbcSource.getConnection();
		} catch (SQLException e) {
			throw new Merge500("SQL Exception connection to data source:" + source);
		}
	}

	// TODO Remove when Get Connection comes from JDBC
	DataSource foo (String a, String b, String c, String d, String e, String f) {
		return null;
	}
	
	@Override
	public DataElement provide(String enrichCommand, Wrapper wrapper, Merger context, HashMap<String,String> replace) throws MergeException {
		Content query = new Content(wrapper, enrichCommand, TagSegment.ENCODE_SQL);
		query.replace(replace, false, context.getConfig().getNestLimit());
		DataList table = new DataList();
		ResultSet results;

		// Execute Command
		try {
			results = this.connection.
					prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).
					executeQuery();
		} catch (SQLException e) {
			throw new Merge500("Invalid SQL Query " + query );
		}

		// Build result table.
		try {
			ResultSetMetaData meta = results.getMetaData();
			while (results.next()) {
				DataObject row = new DataObject();
				for (int column = 1; column <= meta.getColumnCount(); column++) {
					row.put(meta.getColumnName(column), new DataPrimitive(results.getString(column)));
				}
				table.add(row);
			}
			results.close();
		} catch (SQLException e) {
			throw new Merge500("SQL Exception processing results" + query );
		}
		return table;
	}

}
