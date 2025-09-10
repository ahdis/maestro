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

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A Validation Step is an automated step used to validate some content with validation service.
 * This step has 3 properties :
 *  <p>
 *         <b>VALIDATION_SERVICE</b> String name of validation service name.
 *          ServiceRegistryDAO will return a list of services with a name field.
 *          VALIDATION_SERVICE must be in this list.
 *  </p>
 *  <p>
 *          <b>VALIDATION_PROFILE</b> String name of validation Profile provided by validation service
 *  </p>
 *  <p>
 *          <b>CONTENT_TO_VALIDATE</b> Byte array of content to validate with validation profile on validation service
 *  </p>
 * </p>
 */
public class ValidationStep implements Step {

    public static final String TYPE = "VALIDATION";

    public static final String VALIDATION_SERVICE = "validationService";
    public static final String VALIDATION_PROFILE = "validationProfile";
    public static final String CONTENT_TO_VALIDATE = "contentToValidate";

    private String name;
    private Property<String> validationService;
    private Property<String> validationProfile;
    private Property<byte[]> contentToValidate;

    public ValidationStep() {
    }

    public ValidationStep(String name, Property<String> validationService, Property<String> validationProfile, Property<byte[]> contentToValidate) {
        this.name = name;
        this.validationService = validationService;
        this.validationProfile = validationProfile;
        this.contentToValidate = contentToValidate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Property<String> getValidationService() {
        return validationService;
    }

    public Property<String> getValidationProfile() {
        return validationProfile;
    }

    public Property<byte[]> getContentToValidate() {
        return contentToValidate;
    }

    @Override
    public List<Property<?>> getProperties() {
        List<Property<?>> props = new ArrayList<>();
        props.add(validationService);
        props.add(validationProfile);
        props.add(contentToValidate);
        return props;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperties(List<Property<?>> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Property shouldn't be null");
        }
        properties.forEach(property -> {
            switch (property.getName()) {
                case VALIDATION_SERVICE -> validationService = (Property<String>) property;
                case VALIDATION_PROFILE -> validationProfile = (Property<String>) property;
                case CONTENT_TO_VALIDATE -> contentToValidate = (Property<byte[]>) property;
                default -> throw new IllegalArgumentException("Property " + property.getName()
                        + " is not a validation step property");
            }
        });
    }

    @Override
    public String getType() {
        return TYPE;
    }
}