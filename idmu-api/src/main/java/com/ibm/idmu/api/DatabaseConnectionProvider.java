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

/**
 *
 */
public interface DatabaseConnectionProvider {
    void create() throws DatabaseConnectionProviderException;
    void destroy() throws DatabaseConnectionProviderException;
    DatabaseConnectionProvider asNew();
    <T> T runWithPool(SqlOperation<T> sqlOperation) throws DatabaseConnectionProviderException;
    Connection acquireConnection() throws DatabaseConnectionProviderException;
    Map<String, Object> statistics() throws DatabaseConnectionProviderException;

    public static class DatabaseConnectionProviderException extends RuntimeException{
        /**
		 * 
		 */
		private static final long serialVersionUID = -1889891368175097509L;

		public DatabaseConnectionProviderException(String message) {
            super(message);
        }

        public DatabaseConnectionProviderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
