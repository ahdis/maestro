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
import net.ihe.gazelle.evsapi.client.business.ValidationService;
import net.ihe.gazelle.evsapi.client.business.ValidationServiceProfile;
import net.ihe.gazelle.evsapi.client.business.request.HandledObject;
import net.ihe.gazelle.maestro.validation.step.business.ValidationHandler;
import net.ihe.gazelle.maestro.validation.step.technical.mapper.EvsValidationReportMapper;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.List;
import java.util.Collections;

/**
 * ValidationHandler implementation for EVS (External Validation Service)
 * Handles validation through EVS API for various validation types
 */
public class EvsValidationHandler implements ValidationHandler {

    private final ValidationClient validationClient;

    EvsValidationReportMapper evsValidationReportMapper = new EvsValidationReportMapper();

    public EvsValidationHandler(ValidationClient validationClient) {
        this.validationClient = validationClient;
    }

    @Override
    public boolean isAvailable() {
        return getValidationProfiles() != null && !getValidationProfiles().isEmpty();
    }

    @Override
    public ValidationReport validate(ValidationRequest validationRequest, String serviceName) {
        if (validationClient != null) {
            String urlLocation = validationClient.validate(convertToEvsRequest(validationRequest, serviceName));
            net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport evsReport = validationClient.getValidationReportByOid(getOid(urlLocation));
            return evsValidationReportMapper.mapEvsReportToValidationReport(evsReport, validationRequest);
        } else {
            throw new IllegalStateException("Validation client is not set");
        }
    }

    @Override
    public List<ValidationProfile> getValidationProfiles() {
        if (validationClient != null) {
            List<ValidationServiceProfile> validationServiceProfiles = validationClient.getValidationProfiles();
            return convertToValidationProfiles(validationServiceProfiles);
        }
        throw new IllegalStateException("Validation client is not set");
    }

    private net.ihe.gazelle.evsapi.client.business.request.ValidationRequest convertToEvsRequest(ValidationRequest request, String serviceName) {
        return new net.ihe.gazelle.evsapi.client.business.request.ValidationRequest()
                .addValidationItem(
                        new HandledObject()
                                .setContent(request.getValidationItems().getFirst().getContent())
                )
                .setValidationService(
                        new ValidationService()
                                .setName(serviceName)
                                .setValidator(request.getValidationProfileId())
                );
    }

    private List<ValidationProfile> convertToValidationProfiles(List<ValidationServiceProfile> serviceProfiles) {
        if (serviceProfiles == null || serviceProfiles.isEmpty()) {
            return Collections.emptyList();
        }
        return serviceProfiles.stream()
                .map(profile -> new ValidationProfile()
                        .setProfileID(profile.getProfileID())
                        .setProfileName(profile.getServiceName())
                ).toList();
    }

    private String getOid(String urlLocation) {
        return urlLocation.substring(urlLocation.lastIndexOf("/") + 1);
    }}
