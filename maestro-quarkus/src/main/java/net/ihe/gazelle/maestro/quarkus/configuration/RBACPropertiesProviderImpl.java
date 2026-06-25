/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.quarkus.configuration;

import net.ihe.gazelle.security.business.rbac.RBACPropertiesProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads the Maestro default RBAC matrix ({@code /rbac.properties}) so it can be pushed to the
 * permission-manager by the {@code permission-manager-client} plug-and-play lifecycle bean.
 */
public class RBACPropertiesProviderImpl implements RBACPropertiesProvider {

    @Override
    public Properties getRBACProperties() {
        Properties rbacProperties = new Properties();
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream("/rbac.properties")) {
            rbacProperties.load(resourceAsStream);
            return rbacProperties;
        } catch (IOException e) {
            throw new IllegalStateException("Error while retrieving RBAC properties", e);
        }
    }
}
