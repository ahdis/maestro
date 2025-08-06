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

package net.ihe.gazelle.maestro.quarkus.handler;

import net.ihe.gazelle.maestro.validation.step.technical.handler.GazelleValidationServiceHandler;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.List;

public class ValidationHandlerMock extends GazelleValidationServiceHandler {


    public ValidationHandlerMock() {
        /*Empty constructor used for mocking*/
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public ValidationReport validate(ValidationRequest validationRequest, String serviceName) {
        return new ValidationReport();
    }

    @Override
    public List<ValidationProfile> getValidationProfiles() {
        return List.of();
    }
}
