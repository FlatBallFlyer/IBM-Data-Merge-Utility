/*
 * Copyright 2015, 2015 IBM
 * 
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
package com.ibm.util.merge.persistence;

import com.ibm.idmu.api.PoolManager;
import com.ibm.idmu.api.SqlOperation;
import com.ibm.util.merge.directive.AbstractDirective;
import com.ibm.util.merge.directive.Directives;
import com.ibm.util.merge.template.Template;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class JdbcTemplatePersistence implements TemplatePersistence {
    private static final Logger log = Logger.getLogger(JdbcTemplatePersistence.class);
    private static final String ORACLE_SUPPORT_COLUMN = "<null>";
    private PoolManager poolManager;
    private String poolName = "";
    private String schemaName = "";

    public JdbcTemplatePersistence(PoolManager poolManager) {
        this.poolManager = poolManager;
        log.info("Instantiated");
    }

    @Override
    public List<Template> loadAllTemplates() {
        List<Template> templates = poolManager.runWithPool(poolName, new LoadTemplatesSqlOperation(poolManager, poolName, this.getTemplateTableName(), this.getDirectiveTableName()));
        return templates;
    }

    @Override
    public void saveTemplate(final Template template) {
        deleteTemplate(template);
        this.poolManager.runWithPool(poolName, new SaveNewTemplateSqlOperation(template, poolManager, poolName, this.getTemplateTableName(), this.getDirectiveTableName()));
    }

    @Override
    public void deleteTemplate(final Template template) {
        this.poolManager.runWithPool(poolName, new DeleteTemplateSqlOperation(template, this.getTemplateTableName()));
    }

    public void setPoolName(String poolName) {
    	if (poolName != null) {
            this.poolName = poolName;
    	}
    }

    public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		if (schemaName != null) {
			this.schemaName = schemaName;
		}
	}

	private String getTemplateTableName() {
		String name = this.schemaName;
		if (!name.isEmpty()) name += ".";
		name += "IDMU_TEMPLATE";
		return name;
	}

	private String getDirectiveTableName() {
		String name = this.schemaName;
		if (!name.isEmpty()) name += ".";
		name += "IDMU_DIRECTIVE";
		return name;
	}

	public static class LoadTemplatesSqlOperation implements SqlOperation<List<Template>> {
	    private static final Logger log = Logger.getLogger(LoadTemplatesSqlOperation.class);
        private final PoolManager poolManager;
        private final String poolName;
        private final String templateTable;
        private final String directiveTable;

        public LoadTemplatesSqlOperation(PoolManager poolManager, String poolName, String templateTable, String directiveTable) {
        	this.poolManager= poolManager;
        	this.poolName = poolName;
        	this.templateTable = templateTable;
        	this.directiveTable = directiveTable;
        }

    	@Override
        public List<Template> execute(Connection connection) throws SQLException {
            Statement st = null;
            ResultSet rs = null;
            List<Template> out = null;
            String query = "SELECT * FROM " + this.templateTable;
            try {
                st = connection.createStatement();
                rs = st.executeQuery(query);
                out = new ArrayList<>();
                while (rs.next()) {
                    // for each row create a new template instance and set column values
                    Template t = new Template();
                    t.setCollection(rs.getString("COLLECTION"));
                    t.setName(rs.getString("TEMPLATE_NAME"));
                    String columnValue = rs.getString("COLUMN_VALUE"); 
                    t.setColumnValue((columnValue.equals(JdbcTemplatePersistence.ORACLE_SUPPORT_COLUMN) ? "" : columnValue ));
                    t.setOutputFile(rs.getString("OUTPUT_FILE"));
                    t.setDescription(rs.getString("DESCRIPTION"));
                    t.setContent(rs.getString("CONTENT"));
                    this.poolManager.runWithPool(this.poolName, new LoadDirectivesSqlOperation(t, this.directiveTable));
                    out.add(t);
                }
            } catch (SQLException e) {
                log.error("SQL Exception:" + e.getMessage() + " - Query: " + query);
                throw new RuntimeException("Error Loading Template" + query);
            } finally {
                if (rs != null) rs.close();
                if (st != null) st.close();
            }
            return out;
        }
    }

    public static class LoadDirectivesSqlOperation implements SqlOperation<Object> {
	    private static final Logger log = Logger.getLogger(LoadDirectivesSqlOperation.class);
    	private final Template template;
    	private final String directiveTable;
    	
    	public LoadDirectivesSqlOperation(Template template, String directiveTable) {
        	this.template = template;
        	this.directiveTable = directiveTable;
        }

    	@Override
        public Object execute(Connection connection) throws SQLException {
            Statement st = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            List<AbstractDirective> out = null;
            Directives directives = new Directives();
            String query = "SELECT * FROM " + this.directiveTable + " WHERE COLLECTION = ? AND TEMPLATE_NAME = ? AND COLUMN_VALUE = ? ORDER BY SEQUENCE";
            		
            try {
                ps = connection.prepareStatement(query);
                ps.setString(1, this.template.getCollection());
                ps.setString(2, this.template.getName());
                String columnValue = this.template.getColumnValue(); 
                ps.setString(3, (columnValue.isEmpty() ? JdbcTemplatePersistence.ORACLE_SUPPORT_COLUMN : columnValue ));
                ps.execute();
                rs = ps.getResultSet();
                out = new ArrayList<AbstractDirective>();
                while (rs.next()) {
                    // for each row create a new directive instance and set object values
                	AbstractDirective d = directives.getNewDirective(rs.getInt("DIR_TYPE"));
                    d.setSequence(rs.getInt("SEQUENCE"));
                    d.setSoftFail(rs.getString("SOFT_FAIL"));
                    d.setDescription(rs.getString("DESCRIPTION"));
                    d.setD1(rs.getString("DIR_1"));
                    d.setD2(rs.getString("DIR_2"));
                    d.setD3(rs.getString("DIR_3"));
                    d.setD4(rs.getString("DIR_4"));
                    if (d.getProvider() != null) {
	                    d.getProvider().setP1(rs.getString("PRO_1"));
	                    d.getProvider().setP2(rs.getString("PRO_2"));
	                    d.getProvider().setP3(rs.getString("PRO_3"));
	                    d.getProvider().setP4(rs.getString("PRO_4"));
                    }
                    template.addDirective(d);
                }
            } catch (SQLException e) {
                log.error("SQL Exception:" + e.getMessage() + " - Query: " + query);
                throw new RuntimeException("Error Loading Directive" + query);
            } finally {
                if (rs != null) rs.close();
                if (st != null) st.close();
            }
            return out;
        }
    }

    public static class SaveNewTemplateSqlOperation implements SqlOperation<Void> {
	    private static final Logger log = Logger.getLogger(LoadDirectivesSqlOperation.class);
        private final Template template;
        private final PoolManager poolManager;
        private final String poolName;
        private final String templateTable;
        private final String directiveTable;

        public SaveNewTemplateSqlOperation(Template template, PoolManager poolManager, String poolName, String templateTable, String directiveTable) {
            this.template = template;
            this.poolManager = poolManager;
            this.poolName = poolName;
            this.templateTable = templateTable;
            this.directiveTable = directiveTable;
        }

        @Override
        public Void execute(Connection connection) throws SQLException {
            PreparedStatement ps = null;
            String query = "INSERT INTO " + this.templateTable + "(COLLECTION, TEMPLATE_NAME, COLUMN_VALUE, OUTPUT_FILE, DESCRIPTION, CONTENT) VALUES (?, ?, ?, ?, ?, ?)";
            try {
                ps = connection.prepareStatement(query);
                // set the values for each column by order (starts with 1)
                ps.setString(1, template.getCollection());
                ps.setString(2, template.getName());
                String columnValue = template.getColumnValue(); 
                ps.setString(3, (columnValue.isEmpty() ? JdbcTemplatePersistence.ORACLE_SUPPORT_COLUMN : columnValue));
                ps.setString(4, template.getOutput());
                ps.setString(5, template.getDescription());
                ps.setString(6, template.getContent());
                ps.execute();
                for ( AbstractDirective directive : template.getDirectives() ) {
                    this.poolManager.runWithPool(poolName, new SaveNewDirectivesSqlOperation(directive, this.directiveTable));
                }
                return null;
            } catch (Exception e) {
                log.error("SQL Exception:" + e.getMessage() + " - Query: " + query);
                throw new RuntimeException("Error Saving Template" + query);
            } finally {
                if (ps != null) ps.close();
            }
        }
    }

    public static class SaveNewDirectivesSqlOperation implements SqlOperation<Void> {
	    private static final Logger log = Logger.getLogger(SaveNewDirectivesSqlOperation.class);
        private final AbstractDirective directive;
        private final String directiveTable;

        public SaveNewDirectivesSqlOperation(AbstractDirective directive, String directiveTable) {
            this.directive = directive;
            this.directiveTable = directiveTable;
        }

        @Override
        public Void execute(Connection connection) throws SQLException {
            PreparedStatement ps = null;
            String query = "INSERT INTO " + this.directiveTable + " (COLLECTION, TEMPLATE_NAME, COLUMN_VALUE, SEQUENCE, DIR_TYPE, DESCRIPTION, SOFT_FAIL, "
            		+ "DIR_1, DIR_2, DIR_3, DIR_4, "
            		+ "PRO_1, PRO_2, PRO_3, PRO_4) "
            		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                ps = connection.prepareStatement(query);
                // set the values for each column by order (starts with 1)
                ps.setString( 1, directive.getTemplate().getCollection());
                ps.setString( 2, directive.getTemplate().getName());
                String columnValue = directive.getTemplate().getColumnValue(); 
                ps.setString(3, (columnValue.isEmpty() ? JdbcTemplatePersistence.ORACLE_SUPPORT_COLUMN : columnValue));
                ps.setInt(	  4, directive.getSequence());
                ps.setInt(	  5, directive.getType());
                ps.setString( 6, directive.getDescription());
                ps.setString( 7, directive.getSoftFail());
                ps.setString( 8, directive.getD1());
                ps.setString( 9, directive.getD2());
                ps.setString(10, directive.getD3());
                ps.setString(11, directive.getD4());
                if (directive.getProvider() != null) {
                    ps.setString(12, directive.getProvider().getP1());
                    ps.setString(13, directive.getProvider().getP2());
                    ps.setString(14, directive.getProvider().getP3());
                    ps.setString(15, directive.getProvider().getP4());
                } else {
                    ps.setString(12, "");
                    ps.setString(13, "");
                    ps.setString(14, "");
                    ps.setString(15, "");
                }
                ps.execute();
                return null;
            } catch (Exception e) {
                log.error("SQL Exception:" + e.getMessage() + " - Query: " + query);
                throw new RuntimeException("Error creating directive " + directive, e);
            } finally {
                if (ps != null) ps.close();
            }
        }
    }

    public static class DeleteTemplateSqlOperation implements SqlOperation<Void> {
	    private static final Logger log = Logger.getLogger(DeleteTemplateSqlOperation.class);
        private final Template template;
        private final String templateTable;

        public DeleteTemplateSqlOperation(Template template, String templateTable) {
            this.template = template;
            this.templateTable = templateTable;
        }

        @Override
        public Void execute(Connection connection) throws SQLException {
            PreparedStatement ps = null;
            String query = "DELETE FROM " + this.templateTable + " WHERE COLLECTION = ? AND TEMPLATE_NAME = ? AND COLUMN_VALUE = ?";
            try {
                // delete the correct template
                ps = connection.prepareStatement(query);
                ps.setString(1, template.getCollection());
                ps.setString(2, template.getName());
                String columnValue = template.getColumnValue(); 
                ps.setString(3, (columnValue.isEmpty() ? JdbcTemplatePersistence.ORACLE_SUPPORT_COLUMN : columnValue ));
                ps.execute();
                return null;
            } catch (Exception e) {
                log.error("SQL Exception:" + e.getMessage() + " - Query: " + query);
                throw new RuntimeException("Error deleting template " + template, e);
            } finally {
                if (ps != null) ps.close();
            }
        }
    }
}
