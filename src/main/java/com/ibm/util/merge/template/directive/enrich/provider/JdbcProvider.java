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

import com.ibm.util.merge.Config;
import com.ibm.util.merge.data.DataElement;
import com.ibm.util.merge.data.DataList;
import com.ibm.util.merge.data.DataObject;
import com.ibm.util.merge.data.DataPrimitive;
import com.ibm.util.merge.exception.Merge500;
import com.ibm.util.merge.exception.MergeException;
import com.ibm.util.merge.template.Template;
import com.ibm.util.merge.template.content.Content;
import com.ibm.util.merge.template.content.TagSegment;
import com.ibm.util.merge.template.directive.enrich.source.AbstractSource;
import com.ibm.util.merge.template.directive.enrich.source.JdbcSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JdbcProvider extends AbstractProvider {
	private DataSource jndiDataSource;
	
	public JdbcProvider(AbstractSource source) throws MergeException {
		super(source);
		this.setType(AbstractProvider.PROVIDER_JDBC);
	}

	@Override
	public DataElement get(Template template) throws MergeException {
		JdbcSource source = (JdbcSource) this.getSource();
		Content query = new Content(template.getWrapper(), source.getGetCommand(), TagSegment.ENCODE_SQL);
		query.replace(template.getReplaceStack(), false, Config.MAX_NEST);
		DataList table = new DataList();
		try {
			ResultSet results = execute(query.getValue());
			
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
			throw new Merge500("Invalid SQL Query " + query );
		}
		return table;
	}

	@Override
	public void put(Template template) throws MergeException {
		JdbcSource source = (JdbcSource) this.getSource();
		Content query = new Content(template.getWrapper(), source.getPutCommand(), TagSegment.ENCODE_SQL);
		query.replace(template.getReplaceStack(), false, Config.MAX_NEST);
		execute(query.getValue());
	}

	@Override
	public void post(Template template) throws MergeException {
		JdbcSource source = (JdbcSource) this.getSource();
		Content query = new Content(template.getWrapper(), source.getPostCommand(), TagSegment.ENCODE_SQL);
		query.replace(template.getReplaceStack(), false, Config.MAX_NEST);
		execute(query.getValue());
	}

	@Override
	public void delete(Template template) throws MergeException {
		JdbcSource source = (JdbcSource) this.getSource();
		Content query = new Content(template.getWrapper(), source.getDeleteCommand(), TagSegment.ENCODE_SQL);
		query.replace(template.getReplaceStack(), false, Config.MAX_NEST);
		execute(query.getValue());
	}

	private ResultSet execute(String query) throws MergeException {
		try {
			Connection connection = this.getConnection();
			PreparedStatement statement = connection.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet results = statement.executeQuery();
			return results;
		} catch (SQLException e) {
			throw new Merge500("Invalid SQL Query " + query );
		}
	}

	private Connection getConnection() throws MergeException {
    	JdbcSource source = (JdbcSource) this.getSource();
    	if (null == this.jndiDataSource) {
	    	try { 
		    	Context initContext = new InitialContext();
		    	this.jndiDataSource = (DataSource) initContext.lookup(source.getJndiDataSourceName());
		    } catch (NamingException e) {
		    	throw new Merge500("Naming Exception: " + source.getJndiDataSourceName());
		    }
    	}

    	Connection con;
    	try {
			con = this.jndiDataSource.getConnection();
		} catch (SQLException e) {
			throw new Merge500("Error acquiring connection for " + source.getJndiDataSourceName() + ":" + e.getMessage());
		}
    	return con;
    }
    
}

