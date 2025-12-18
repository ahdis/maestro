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

import jakarta.enterprise.inject.spi.CDI;
import net.ihe.gazelle.oidc.server.OIDCIdentity;
import net.ihe.gazelle.security.business.Authenticator;
import net.ihe.gazelle.security.business.BaseGazelleIdentity;
import net.ihe.gazelle.security.business.GazelleIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;

public class WebSocketAuthenticator implements Authenticator {

   public WebSocketAuthenticator() {
      // Used for SPI
   }

   @Override
   public int getWeight() {
      return 300;
   }

   @Override
   public GazelleIdentity authenticate() {
      JwtContext jwtContext = getJwtContext();
      JsonWebToken jwt = jwtContext.get();
      if (jwt != null && jwt.getClaimNames() != null && jwt.getClaim("authn_method").equals("OIDC")) {

         return new OIDCIdentity(jwt)
               .setId(jwt.getName())
               .setName(jwt.getSubject());
      } else {
         return BaseGazelleIdentity.unauthenticatedIdentity();
      }
   }

   private JwtContext getJwtContext() {
      return CDI.current().select(JwtContext.class).get();
   }
}
