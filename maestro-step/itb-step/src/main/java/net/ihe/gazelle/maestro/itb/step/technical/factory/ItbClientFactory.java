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

package net.ihe.gazelle.maestro.itb.step.technical.factory;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.itb.gateway.technical.client.ItbClient;
import net.ihe.gazelle.maestro.itb.step.business.ItbHandler;
import net.ihe.gazelle.maestro.spi.technical.HandlerProvider;

/**
 * CDI Factory to produce ItbClient instances for the Callback controller bean.
 * <p>
 * <i>This factory is wrapping a call to the HandlerProvider to maintain only one way of instantiating the client ITB
 * handler.</i>
 */
public class ItbClientFactory {

   private final HandlerProvider handlerProvider;

   @Inject
   public ItbClientFactory(HandlerProvider handlerProvider) {
      this.handlerProvider = handlerProvider;
   }

   @Produces
   @RequestScoped
   public ItbClient createItbClient() {
      return (ItbClient) handlerProvider.getHandler(ItbHandler.ITB_SERVICE_NAME, ItbHandler.class);
   }

}
