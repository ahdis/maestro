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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ClientEndpoint
@ApplicationScoped
public class TestWebSocketClient {

    private final TextSerDes serDes = new JacksonSerDes();
    private final CompletableFuture<ExecutionFinished> future = new CompletableFuture<>();
    private final CompletableFuture<String> errorFuture = new CompletableFuture<>();

    @OnMessage
    public void onMessage(String msg) {
        try {
            MessageDTO<?> message = serDes.deserialize(msg, MessageDTO.class);
            if (message instanceof ExecutionFinishedDTO executionFinishedDTO) {
                future.complete(executionFinishedDTO.getBusinessObject());
            }
        } catch (DeserializationException e) {
            // Do nothing
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        errorFuture.complete(reason.getReasonPhrase());
    }

    public TestReport getTestReport() {
        try {
            return future.get(2, TimeUnit.SECONDS).getReport();
        } catch (TimeoutException e) {
            throw new TimeoutRuntimeException(e);
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public String getError() {
        try {
            return errorFuture.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new TimeoutRuntimeException(e);
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
