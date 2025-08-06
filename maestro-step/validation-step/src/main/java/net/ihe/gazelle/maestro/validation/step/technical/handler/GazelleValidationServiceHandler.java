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

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.validation.step.business.ServiceClientException;
import net.ihe.gazelle.maestro.validation.step.business.ValidationHandler;
import net.ihe.gazelle.validation.v2.api.business.ValidationService;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.List;

public class GazelleValidationServiceHandler implements ValidationHandler {

    private ValidationService validationService;

    protected GazelleValidationServiceHandler() {
        /*Empty constructor used for mocking*/
    }

    public GazelleValidationServiceHandler(ValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Override from {@link Handler} Interface
     */
    @Override
    public boolean isAvailable() {
        return validationService.getValidationProfiles() != null;
    }

    public ValidationReport validate(ValidationRequest validationRequest, String serviceName) {
        if (validationService != null) {
            return validationService.validate(validationRequest);
        } else {
            throw new ServiceClientException("validationService not set");
        }
    }

    public List<ValidationProfile> getValidationProfiles() {
        if (validationService != null) {
            return validationService.getValidationProfiles();
        } else {
            throw new ServiceClientException("validationService not set");
        }
    }
}
