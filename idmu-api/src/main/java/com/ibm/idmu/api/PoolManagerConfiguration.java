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
package com.ibm.idmu.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 *
 */
public class PoolManagerConfiguration {
    public static final String POOLCONFIG_URL = "url";
    public static final String POOLCONFIG_DRIVER = "driver";
    public static final String POOLCONFIG_USERNAME = "username";
    public static final String POOLCONFIG_PASSWORD = "password";
    public static final String POOLCONFIG_PROPERTIESPATH = "propertiesPath";
    public static final String POOLCONFIG_POOLNAMES = "poolNames";
    public static final String POOLCONFIG_DEFAULTDRIVER = "defaultDriver";
	public static final String POOLCONFIG_POOLNAME = "name";
	private String defaultJdbcDriver;
    private Map<String, Map<String, String>> poolConfigs;

    public PoolManagerConfiguration(String defaultJdbcDriver, Map<String, Map<String, String>> poolConfigs) {
        this.defaultJdbcDriver = defaultJdbcDriver;
        this.poolConfigs = poolConfigs;
    }

    public static PoolManagerConfiguration fromPropertiesFile(File file) {
        return fromProperties(loadProperties(file));
    }

    public static PoolManagerConfiguration fromProperties(Properties props) {
        String defaultDriver = props.getProperty(POOLCONFIG_DEFAULTDRIVER);
        String poolNamesString = props.getProperty(POOLCONFIG_POOLNAMES);
        String[] poolNames = poolNamesString.split(",");
        Map<String, Map<String, String>> pc = new HashMap<>();
        for (String pn : poolNames) {
			String parent = pn + ".";
            String url = props.getProperty(parent + POOLCONFIG_URL);
            if (url == null) throw new IllegalArgumentException("Illegal pool configuration: pool " + pn + " does not have a jdbc url at " + pn + ".url");
            Map<String, String> cfg = new TreeMap<>();
            pc.put(pn, cfg);
            cfg.put(POOLCONFIG_POOLNAME, pn);
            cfg.put(POOLCONFIG_URL, url);
            String driver = props.getProperty(parent + POOLCONFIG_DRIVER);
            if (driver != null) {
                cfg.put(POOLCONFIG_DRIVER, driver);
            }
            String username = props.getProperty(parent + POOLCONFIG_USERNAME);
            if (username != null) {
                cfg.put(POOLCONFIG_USERNAME, username);
            }
            String password = props.getProperty(parent + POOLCONFIG_PASSWORD);
            if (password != null) {
                cfg.put(POOLCONFIG_PASSWORD, password);
            }
            String propertiesPath = props.getProperty(parent + POOLCONFIG_PROPERTIESPATH);
            if (propertiesPath != null) {
                cfg.put(POOLCONFIG_PROPERTIESPATH, propertiesPath);
            }
        }
        return new PoolManagerConfiguration(defaultDriver, pc);
    }

    public static Properties loadProperties(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Properties p1 = new Properties();
            p1.load(fis);
            return p1;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not load properties at " + file.getPath(), e);
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties at " + file.getPath(), e);
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getDefaultJdbcDriver() {
        return defaultJdbcDriver;
    }

    public Set<String> listPoolNames() {
        return new TreeSet<>(this.poolConfigs.keySet());
    }

    public Map<String, Map<String, String>> getPoolConfigs() {
        return new TreeMap<>(poolConfigs);
    }
}
