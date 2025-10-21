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

package net.ihe.gazelle.maestro.validation.step.technical.handler;


import net.ihe.gazelle.evsapi.client.business.ValidationClient;
import net.ihe.gazelle.evsapi.client.business.ValidationServiceProfile;
import net.ihe.gazelle.evsapi.client.business.request.ValidationRequest;
import net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport;
import net.ihe.gazelle.maestro.spi.business.runner.Handler;

import java.util.List;

/**
 * ValidationHandler implementation for EVS (External Validation Service)
 * Handles validation through EVS API for various validation types
 */
public class EvsValidationHandler implements Handler {

    private final ValidationClient validationClient;

    public EvsValidationHandler(ValidationClient validationClient) {
        this.validationClient = validationClient;
    }

    @Override
    public boolean isAvailable() {
        return getValidationProfiles() != null && !getValidationProfiles().isEmpty();
    }

    public ValidationReport validate(ValidationRequest evsRequest) {
        if (validationClient == null) {
            throw new IllegalStateException("Validation client is not set");
        }
        if (evsRequest == null) {
            throw new IllegalArgumentException("Validation request cannot be null");
        }
        String urlLocation = validationClient.validate(evsRequest);
        return validationClient.getValidationReportByOid(getOid(urlLocation));
    }

    public List<ValidationServiceProfile> getValidationProfiles() {
        if (validationClient == null) {
            throw new IllegalStateException("Validation client is not set");
        }
        return validationClient.getValidationProfiles();

    }


    private String getOid(String urlLocation) {
        return urlLocation.substring(urlLocation.lastIndexOf("/") + 1);
    }}
