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

package net.ihe.gazelle.maestro.validation.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.List;

/**
 * Interface for validation handlers that can validate content
 * This interface supports multiple types of validation services
 */
public interface ValidationHandler extends Handler {

    /**
     * Validates the given request and returns a validation report
     * @param validationRequest The validation request
     * @return The validation report
     */
    ValidationReport validate(ValidationRequest validationRequest, String serviceName);

    /**
     * Gets the available validation profiles for this handler
     * @return List of validation profiles
     */
    List<ValidationProfile> getValidationProfiles();
}
