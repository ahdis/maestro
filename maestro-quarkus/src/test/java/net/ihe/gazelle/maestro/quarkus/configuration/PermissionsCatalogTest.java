/*
 * Copyright 2025-2026 IHE International.
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
 */

package net.ihe.gazelle.maestro.quarkus.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PermissionsCatalogTest {

   @Test
   void executeTestDefaultsToGazelleAdministratorsAndPublicAccess() throws IOException {
      try (InputStream catalog = getClass().getResourceAsStream("/permissions.json")) {
         assertNotNull(catalog);
         JsonNode permissions = new ObjectMapper().readTree(catalog).path("permissions");
         JsonNode executeTest = permissions.get(0);
         assertEquals("test:run", executeTest.path("key").asText());
         Set<String> groups = new ObjectMapper().convertValue(
               executeTest.path("policies").get(0).path("groups"),
               new com.fasterxml.jackson.core.type.TypeReference<>() { });

         assertEquals(Set.of("role:gazelle_admin", "role:anonymous"), groups);
      }
   }
}
