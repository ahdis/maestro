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
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Factory class responsible for producing a {@link JsonWebToken} for the current request scope.
 */
@RequestScoped
public class JsonWebTokenFactory {

   private final JwtContext jwtContext;

   /**
    * Constructs a new {@code JsonWebTokenFactory} with the given {@link JwtContext}.
    *
    * @param jwtContext the context used to retrieve the {@link JsonWebToken} for the current request
    */
   @Inject
   public JsonWebTokenFactory(JwtContext jwtContext) {
      this.jwtContext = jwtContext;
   }

   /**
    * Produces the {@link JsonWebToken} associated with the current request.
    *
    * @return the {@link JsonWebToken} from the {@link JwtContext}
    */
   @Produces
   @RequestScoped
   public JsonWebToken produceJwt() {
      return jwtContext.get();
   }
}
