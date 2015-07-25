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
package com.ibm.util.merge.db;

import com.ibm.idmu.api.DatabaseConnectionProvider;
import com.ibm.idmu.api.PoolManager;
import com.ibm.idmu.api.PoolManagerConfiguration;
import com.ibm.idmu.api.SqlOperation;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the connection pools. A single instance should be managed
 */
public class ConnectionPoolManager implements PoolManager {

    private final Map<String, DatabaseConnectionProvider> connectionProviders = new ConcurrentHashMap<>();
    private String poolingDriverClasspath;

    public ConnectionPoolManager() {
        poolingDriverClasspath = "org.apache.commons.dbcp2.PoolingDriver";
        try {
            loadPoolingDriverImplementation();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("commons-dbcp2 not found on classpath", e);
        }
    }

    public ConnectionPoolManager(String poolingDriverClasspath) {
        this.poolingDriverClasspath = poolingDriverClasspath;
    }

    @Override
    public void applyConfig(PoolManagerConfiguration cfg) throws PoolManagerException {
        String defaultDriver = cfg.getDefaultJdbcDriver();
        if(defaultDriver != null && !defaultDriver.trim().isEmpty()){
            try {
                loadDriverClass(defaultDriver);
            } catch (ClassNotFoundException e) {
                throw new PoolManagerException("Could not load specified defaultDriver : " + defaultDriver, e);
            }
        }
        for (Map<String, String> poolConfig : cfg.getPoolConfigs().values()) {
            String name = poolConfig.get(PoolManagerConfiguration.POOLCONFIG_POOLNAME);
            if(name == null) throw new IllegalArgumentException("missing name property for poolconfig");
            String url = poolConfig.get(PoolManagerConfiguration.POOLCONFIG_URL);
            if(url == null) throw new IllegalArgumentException("missing url property for poolconfig " + name);
            String driver = poolConfig.get(PoolManagerConfiguration.POOLCONFIG_DRIVER);
            if(driver != null && !driver.isEmpty()){
                try {
                    loadDriverClass(driver);
                } catch (ClassNotFoundException e) {
                    throw new PoolManagerException("Could not load specified driver for pool "+name+"  : " + defaultDriver, e);
                }
            }
            String propertiesPath = poolConfig.get(PoolManagerConfiguration.POOLCONFIG_PROPERTIESPATH);
            String username = poolConfig.get(PoolManagerConfiguration.POOLCONFIG_USERNAME);
            String password = poolConfig.get(PoolManagerConfiguration.POOLCONFIG_PASSWORD);
            if(propertiesPath != null){
                File file = new File(propertiesPath);
                Properties p = PoolManagerConfiguration.loadProperties(file);
                createPool(name, url, p);
            }else if(username != null){
                createPool(name, url, username, password);
            }else{
                createPool(name, url);
            }
        }
    }

    private void loadPoolingDriverImplementation() throws ClassNotFoundException {
        loadDriverClass(poolingDriverClasspath);
    }

    @Override
    public final void closePool(String poolName) throws PoolManagerException {
        if(!isPoolName(poolName)) throw new IllegalArgumentException("There is no pool named " + poolName);
        DatabaseConnectionProvider p = connectionProviders.remove(poolName);
        p.destroy();
    }

    @Override
    public boolean isPoolName(String poolName) {
        return connectionProviders.containsKey(poolName);
    }


    @Override
    public final void createPool(String poolName, String jdbcConnectionUrl) throws PoolManagerException {
        if(isPoolName(poolName)) throw new IllegalArgumentException("poolName " + poolName + " already exists");
        JdbcDatabaseConnectionProvider p1 = new JdbcDatabaseConnectionProvider(poolName, jdbcConnectionUrl);
        p1.create();
        connectionProviders.put(poolName, p1);
    }
    @Override
    public final void createPool(String poolName, String jdbcConnectionUrl, String username, String password) throws PoolManagerException {
        if(isPoolName(poolName)) throw new IllegalArgumentException("poolName " + poolName + " already exists");
        JdbcDatabaseConnectionProvider p1 = new JdbcDatabaseConnectionProvider(poolName, jdbcConnectionUrl, username, password);
        p1.create();
        connectionProviders.put(poolName, p1);
    }
    @Override
    public final void createPool(String poolName, String jdbcConnectionUrl, Properties properties) throws PoolManagerException {
        if(isPoolName(poolName)) throw new IllegalArgumentException("poolName " + poolName + " already exists");
        JdbcDatabaseConnectionProvider p1 = new JdbcDatabaseConnectionProvider(poolName, jdbcConnectionUrl, properties);
        p1.create();
        connectionProviders.put(poolName, p1);
    }

    @Override
    public final <T> T runWithPool(String poolName, SqlOperation<T> sqlOperation) throws PoolManagerException {
        DatabaseConnectionProvider provider = connectionProviders.get(poolName);
        if(provider == null) throw new PoolManagerException("Unknown poolName: "+ poolName);
        return provider.runWithPool(sqlOperation);
    }

    @Override
    public final void loadDriverClass(String driverClassPath) throws ClassNotFoundException {
        Class<?> driverClass = Class.forName(driverClassPath);
        System.out.println("Loaded class for " + driverClassPath + ": " + driverClass.getName());
    }

    @Override
    public Map<String, Object> statistics(String poolName) throws PoolManagerException {
        return connectionProviders.get(poolName).statistics();
    }

    @Override
    public Connection acquireConnection(String poolName){
        return connectionProviders.get(poolName).acquireConnection();
    }

    @Override
    public final void reset(){
        for (String poolName : connectionProviders.keySet()) {
            try {
                closePool(poolName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connectionProviders.clear();
    }

    public static class LoadConnectionPoolPropertiesException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private final String name;
        private final String propertiesPath;

        public LoadConnectionPoolPropertiesException(String name, String propertiesPath, Exception e) {
            super("Error loading properties for pool " + name + " at path " + propertiesPath, e);
            this.name = name;
            this.propertiesPath = propertiesPath;
        }

        public String getName() {
            return name;
        }

        public String getPropertiesPath() {
            return propertiesPath;
        }
    }
}
