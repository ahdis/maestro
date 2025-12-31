/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.maestro.quarkus.websocket;

import jakarta.enterprise.context.RequestScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Holds the {@link JsonWebToken} for the current request scope.
 */
@RequestScoped
public class JwtContext {

   private JsonWebToken jwt;

   /**
    * Default constructor
    */
   public JwtContext() {
      // Empty
   }

   /**
    * Retrieves the {@link JsonWebToken} for the current request.
    *
    * @return the current {@link JsonWebToken}, or {@code null} if none has been set
    */
   public JsonWebToken get() {
      return jwt;
   }

   /**
    * Sets the {@link JsonWebToken} for the current request.
    *
    * @param jwt the {@link JsonWebToken} to associate with the current request
    */
   public void set(JsonWebToken jwt) {
      this.jwt = jwt;
   }
}
