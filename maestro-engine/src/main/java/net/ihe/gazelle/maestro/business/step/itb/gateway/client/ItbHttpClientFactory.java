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

package net.ihe.gazelle.maestro.business.step.itb.gateway.client;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class ItbHttpClientFactory {

    @ConfigProperty(name = "itb.api.url")
    String itbApiUrl;

    @ConfigProperty(name = "itb.api.key")
    String itbApiKey;

    @Produces
    @RequestScoped
    public ItbHttpClient createItbHttpClient() {
        if (itbApiUrl == null || itbApiUrl.isBlank()) {
            throw new IllegalArgumentException("ITB API URL is not configured");
        }
        if (itbApiKey == null || itbApiKey.isBlank()) {
            throw new IllegalArgumentException("ITB API Key is not configured");
        }

        return new ItbHttpClient(itbApiUrl, itbApiKey);
    }
}
