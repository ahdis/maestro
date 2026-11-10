/*
 * Copyright 2026 IHE International.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
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
 */

package net.ihe.gazelle.maestro.quarkus.dao;

import java.io.Serial;

/**
 * Runtime exception thrown when a required connection to an external data store
 * or service cannot be established, retrieved, or used.
 */
public class ConnectionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7577756477953420956L;

    /**
     * Creates a new connection exception with the specified detail message.
     *
     * @param message the detail message describing the connection failure
     */
    public ConnectionException(String message) {
        super(message);
    }

    /**
     * Creates a new connection exception with the specified detail message and cause.
     *
     * @param message the detail message describing the connection failure
     * @param cause the underlying cause of the connection failure
     */
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
