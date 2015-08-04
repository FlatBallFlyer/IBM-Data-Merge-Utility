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
    private Logger log = Logger.getLogger(JdbcTemplatePersistence.class);
    private PoolManager poolManager;
    private String poolName;

    public JdbcTemplatePersistence(PoolManager poolManager, String poolName) {
        this(poolManager);
        this.poolName = poolName;
        log.info("Instantiated");
    }

    public JdbcTemplatePersistence(PoolManager poolManager) {
        this.poolManager = poolManager;
        log.info("Instantiated");
    }

    @Override
    public List<Template> loadAllTemplates() {
        List<Template> templates = poolManager.runWithPool(poolName, new LoadTemplatesSqlOperation());
        return templates;
    }

    @Override
    public void saveTemplate(final Template template) {
        deleteTemplate(template);
        this.poolManager.runWithPool(poolName, new SaveNewTemplateSqlOperation(template));
    }

    @Override
    public void deleteTemplate(final Template template) {
        this.poolManager.runWithPool(poolName, new DeleteTemplateSqlOperation(template));
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public static class LoadTemplatesSqlOperation implements SqlOperation<List<Template>> {
        @Override
        public List<Template> execute(Connection connection) throws SQLException {
            Statement st = null;
            ResultSet rs = null;
            List<Template> out = null;
            try {
                st = connection.createStatement();
                rs = st.executeQuery("SELECT * FROM TEMPLATE");
                out = new ArrayList<>();
                while (rs.next()) {
                    // for each row create a new template instance and set column values
                    Template t = new Template();
                    long id = rs.getLong("ID_TEMPLATE");
                    t.setIdtemplate(id);
                    t.setCollection(rs.getString("COLLECTION"));
                    t.setName(rs.getString("TEMPLATE_NAME"));
                    t.setColumnValue(rs.getString("COLUMN_VALUE"));
                    t.setOutputFile(rs.getString("OUTPUT"));
                    t.setDescription(rs.getString("DESCRIPTION"));
                    t.setContent(rs.getString("CONTENT"));
                    out.add(t);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (st != null) st.close();
            }
            return out;
        }
    }

    public static class SaveNewTemplateSqlOperation implements SqlOperation<Void> {
        private final Template template;

        public SaveNewTemplateSqlOperation(Template template) {
            this.template = template;
        }

        @Override
        public Void execute(Connection connection) throws SQLException {
            PreparedStatement ps = null;
            try {

                String query = "INSERT INTO TEMPLATE(COLLECTION, TEMPLATE_NAME, COLUMN_VALUE, OUTPUT, DESCRIPTION, CONTENT) VALUES (?, ?, ?, ?, ?, ?)";
                ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                // set the values for each column by order (starts with 1)
                ps.setString(1, template.getCollection());
                ps.setString(2, template.getName());
                ps.setString(3, template.getColumnValue());
                ps.setString(4, template.getOutput());
                ps.setString(5, template.getDescription());
                ps.setString(6, template.getContent());
                ps.execute();
                ResultSet rs = ps.getGeneratedKeys();

                if (rs != null && rs.next()) {
                    long generatedTemplateId = rs.getLong(1);
                    template.setIdtemplate(generatedTemplateId);
                }else{
                    throw new IllegalStateException("Did not get generated template id from DB for " + template);
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException("Error creating template " + template, e);
            } finally {
                if (ps != null) ps.close();
            }
        }
    }

    public static class DeleteTemplateSqlOperation implements SqlOperation<Void> {
        private final Template template;

        public DeleteTemplateSqlOperation(Template template) {
            this.template = template;
        }

        @Override
        public Void execute(Connection connection) throws SQLException {
            PreparedStatement ps = null;
            try {
                // delete the correct template
                ps = connection.prepareStatement("DELETE FROM TEMPLATE WHERE ID_TEMPLATE = ?");
                // set unique identifier for template
                ps.setLong(1, template.getIdtemplate());
                ps.execute();
                int updated = ps.getUpdateCount();
                if (updated < 1) {
                    throw new IllegalArgumentException("Delete didn't update table");
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException("Error deleting template " + template, e);
            } finally {
                if (ps != null) ps.close();
            }
        }
    }
}
