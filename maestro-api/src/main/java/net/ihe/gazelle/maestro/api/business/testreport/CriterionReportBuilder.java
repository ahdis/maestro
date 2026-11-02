/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.CriterionReportValidator;

/**
 * Builder for {@link CriterionReport}.
 */
public class CriterionReportBuilder extends AbstractBuilder<CriterionReport> {

    private String name;
    private CriterionResult criterionResult;

    /**
     * Default constructor.
     */
    public CriterionReportBuilder() {
        super();
    }

    /**
     * Constructs an instance of {@code CriterionReportBuilder} with a provided {@code ValidatorBuilderFactory}.
     *
     * @param validatorBuilderFactory the factory to be used for creating validators
     */
    public CriterionReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    /**
     * Constructs an instance of {@code CriterionReportBuilder} using an existing {@code CriteriaReport}.
     *
     * @param criterionReport the criteria report object whose values will
     *                              be used to initialize this builder; can be null
     */
    public CriterionReportBuilder(CriterionReport criterionReport) {
        if (criterionReport != null) {
            this.name = criterionReport.getDescription();
            this.criterionResult = criterionReport.getCriterionResult();
        }
    }

    /**
     * Sets the name for the criteria report.
     *
     * @param name the name to set for the criteria report
     * @return the current instance of {@code CriterionReportBuilder} to allow method chaining
     */
    public CriterionReportBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the grading result for the criteria report.
     *
     * @param criterionResult the grading result to set; must be a valid {@link CriterionResult} value
     * @return the current instance of {@code CriterionReportBuilder} to allow method chaining
     */
    public CriterionReportBuilder setGradingResult(CriterionResult criterionResult) {
        this.criterionResult = criterionResult;
        return this;
    }

    @Override
    protected AbstractValidator<CriterionReport> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new CriterionReportValidator(validatorBuilderFactory);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected CriterionReport instantiate() {
        return new CriterionReport();
    }

    @Override
    protected void make(CriterionReport criterionReport) {
        criterionReport
                .setDescription(name)
                .setCriterionResult(criterionResult);
    }
}
