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

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public interface PoolManager {
    void applyConfig(PoolManagerConfiguration cfg) throws PoolManagerException;
    void closePool(String poolName) throws PoolManagerException;
    boolean isPoolName(String poolName);
    void createPool(String poolName, String jdbcConnectionUrl) throws PoolManagerException;
    void createPool(String poolName, String jdbcConnectionUrl, String username, String password) throws PoolManagerException;
    void createPool(String poolName, String jdbcConnectionUrl, Properties properties) throws PoolManagerException;
    <T> T runWithPool(String poolName, SqlOperation<T> sqlOperation) throws PoolManagerException;
    void loadDriverClass(String driverClassPath) throws ClassNotFoundException;
    Map<String, Object> statistics(String poolName) throws PoolManagerException;
    Connection acquireConnection(String poolName);
    void reset();

    public static class PoolManagerException extends RuntimeException{
        /**
		 * 
		 */
		private static final long serialVersionUID = -8439762899883280165L;

		public PoolManagerException(String message) {
            super(message);
        }

        public PoolManagerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
