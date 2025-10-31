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

import net.ihe.gazelle.maestro.api.business.test.SupportedFileInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.runner.StepDefinition;

import java.io.Serial;
import java.util.List;

/**
 * <p>
 * A Validation Step is an automated step used to validate some content with validation service. This step has 3
 * properties :
 * </p>
 * <p>
 * <b>VALIDATION_SERVICE</b> String name of validation service name.
 * ServiceRegistryDAO will return a list of services with a name field. VALIDATION_SERVICE must be in this list.
 * </p>
 * <p>
 * <b>VALIDATION_PROFILE</b> String name of validation Profile provided by validation service
 * </p>
 * <p>
 * <b>CONTENT_TO_VALIDATE</b> Byte array of content to validate with validation profile on validation service
 * </p>
 */
public class ValidationStep implements StepDefinition {

   public static final String TYPE = "VALIDATION";

   public static final String VALIDATION_SERVICE = "validationService";
   public static final String VALIDATION_PROFILE = "validationProfile";
   public static final String CONTENT_TO_VALIDATE = "contentToValidate";

   @Serial
   private static final long serialVersionUID = 7657361003088468932L;

   public ValidationStep() {
   }

   @Override
   public String getType() {
      return TYPE;
   }

   @Override
   public List<SupportedInput> getSupportedInputs() {
      return List.of(
            new SupportedTextInput().setId(VALIDATION_SERVICE).setLabel("Validation service").setRequired(true),
            new SupportedTextInput().setId(VALIDATION_PROFILE).setLabel("Validation profile").setRequired(true),
            // CONTENT_TO_VALIDATE is not required because it's only a fallback in case the validation profile does not
            // define any expected inputs.
            new SupportedFileInput().setId(CONTENT_TO_VALIDATE).setLabel("Content to validate").setRequired(false)
      );
   }

}