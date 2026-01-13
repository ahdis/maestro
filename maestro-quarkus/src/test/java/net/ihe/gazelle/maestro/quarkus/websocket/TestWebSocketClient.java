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

package net.ihe.gazelle.maestro.quarkus.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.lang.TimeoutRuntimeException;
import net.ihe.gazelle.maestro.api.business.message.ExecutionFinished;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.quarkus.websocket.dto.ExecutionFinishedDTO;
import net.ihe.gazelle.maestro.quarkus.websocket.dto.MessageDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.DeserializationException;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;

import java.util.Map;
import java.util.concurrent.*;

@ClientEndpoint
@ApplicationScoped
public class TestWebSocketClient {

   private final TextSerDes serDes = new JacksonSerDes();
   private static final Map<String, CompletableFuture<ExecutionFinished>> EXECUTIONS = new ConcurrentHashMap<>();
   private static final Map<String, CompletableFuture<String>> ERRORS = new ConcurrentHashMap<>();

   @OnOpen
   public void onOpen(Session session) {
      EXECUTIONS.put(session.getId(), new CompletableFuture<>());
      ERRORS.put(session.getId(), new CompletableFuture<>());
   }

   @OnMessage
   public void onMessage(Session session, String msg) {
      try {
         MessageDTO<?> message = serDes.deserialize(msg, MessageDTO.class);
         if (message instanceof ExecutionFinishedDTO executionFinishedDTO) {
            EXECUTIONS.get(session.getId()).complete(executionFinishedDTO.getBusinessObject());

         }
      } catch (DeserializationException e) {
         // Do nothing
      }
   }

   @OnClose
   public void onClose(Session session, CloseReason reason) {
      ERRORS.get(session.getId()).complete(reason.getReasonPhrase());
   }

   public TestReport getTestReport(String sessionId) {
      try {
         return EXECUTIONS.get(sessionId).get(2, TimeUnit.SECONDS).getReport();
      } catch (TimeoutException e) {
         throw new TimeoutRuntimeException(e);
      } catch (ExecutionException e) {
         throw new ExecutionRuntimeException(e);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         return null;
      }
   }

   public String getError(String sessionId) {
      try {
         return ERRORS.get(sessionId).get(2, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
         throw new TimeoutRuntimeException(e);
      } catch (ExecutionException e) {
         throw new ExecutionRuntimeException(e);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         return null;
      }
   }

   public static void clear(String sessionId) {
      EXECUTIONS.remove(sessionId);
      ERRORS.remove(sessionId);
   }
}
