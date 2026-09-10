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

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.security.business.Permission;
import net.ihe.gazelle.security.business.PermissionStore;
import net.ihe.gazelle.security.business.rbac.RBACMatrixProvider;
import net.ihe.gazelle.security.business.rbac.RBACPolicy;

import java.util.Set;

@ApplicationScoped
public class MaestroPermissionStore implements PermissionStore {

   private final RBACMatrixProvider rbacMatrixProvider;

   public MaestroPermissionStore(RBACMatrixProvider rbacMatrixProvider) {
      this.rbacMatrixProvider = rbacMatrixProvider;
   }

   @Override
   public Set<Permission> getPermissions() {
      return Set.of(
            new Permission(MaestroPermissions.TEST_RUN, new RBACPolicy(MaestroPermissions.TEST_RUN, rbacMatrixProvider))
      );
   }
}
