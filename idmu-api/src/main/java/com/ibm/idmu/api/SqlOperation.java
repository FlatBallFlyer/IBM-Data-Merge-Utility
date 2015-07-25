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
import java.sql.SQLException;

/**
 * An operation that requires access to a java.sql.Connection
 * Connection lifecycle management happens outside of the operation, so the Connection instance should not be closed
 * The generic T parameter identifies the result of the operation (use Void for null)
 */
public interface SqlOperation<T> {
    T execute(Connection connection) throws SQLException;
}
