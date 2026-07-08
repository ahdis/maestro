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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.security.business.rbac.RBACPropertiesProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads the Maestro default permission declarations ({@code /permissions.json}) so they can be pushed to the
 * permission-manager by the {@code permission-manager-client} plug-and-play lifecycle bean.
 */
public class RBACPropertiesProviderImpl implements RBACPropertiesProvider {
    private static final String PERMISSIONS_RESOURCE = "/permissions.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Properties getRBACProperties() {
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream(PERMISSIONS_RESOURCE)) {
            if (resourceAsStream == null) {
                throw new IllegalStateException(PERMISSIONS_RESOURCE + " is missing");
            }
            PermissionCatalog catalog = objectMapper.readValue(resourceAsStream, PermissionCatalog.class);
            return catalog.toRBACProperties();
        } catch (IOException e) {
            throw new IllegalStateException("Error while retrieving RBAC permissions", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PermissionCatalog(List<PermissionEntry> permissions) {
        Properties toRBACProperties() {
            Properties properties = new Properties();
            if (permissions == null) {
                return properties;
            }
            Map<String, Set<String>> matrix = new java.util.LinkedHashMap<>();
            permissions.forEach(permission -> {
                Set<String> groups = permission.rbacGroups();
                matrix.merge(permission.key(), groups, PermissionCatalog::merge);
                permission.requiredPermissions().forEach(required ->
                        matrix.merge(required, groups, PermissionCatalog::merge));
            });
            matrix.forEach((key, groups) -> properties.setProperty(key,
                    groups.stream().sorted().collect(Collectors.joining(","))));
            return properties;
        }

        private static Set<String> merge(Set<String> first, Set<String> second) {
            Set<String> merged = new HashSet<>(first);
            merged.addAll(second);
            return Set.copyOf(merged);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PermissionEntry(String key, Set<String> groups, List<PolicyEntry> policies) {
        PermissionEntry {
            groups = groups == null ? Set.of() : Set.copyOf(groups);
            policies = policies == null ? List.of() : List.copyOf(policies);
        }

        Set<String> rbacGroups() {
            Set<String> values = new HashSet<>(groups);
            policies.stream()
                    .filter(policy -> "RBAC".equalsIgnoreCase(policy.type()))
                    .flatMap(policy -> policy.groups().stream())
                    .forEach(values::add);
            return Set.copyOf(values);
        }

        Set<String> requiredPermissions() {
            return policies.stream()
                    .filter(policy -> "PERMISSION_DEPENDENCY".equals(policy.type().toUpperCase(Locale.ROOT)))
                    .flatMap(policy -> policy.permissions().stream())
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PolicyEntry(String type, Set<String> groups, Set<String> permissions) {
        PolicyEntry {
            type = type == null ? "" : type;
            groups = groups == null ? Set.of() : Set.copyOf(groups);
            permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
        }
    }
}
