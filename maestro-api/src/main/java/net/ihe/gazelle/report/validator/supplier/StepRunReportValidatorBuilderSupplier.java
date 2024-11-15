package net.ihe.gazelle.report.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.Item;
import net.ihe.gazelle.report.model.StepRunReport;

public class StepRunReportValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<StepRunReport> {

    public StepRunReportValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends StepRunReport> getType() {
        return StepRunReport.class;
    }

    @Override
    protected ValidatorBuilder<StepRunReport> supplyRulesAndMembers(ValidatorBuilder<StepRunReport> validatorBuilder) {
        return validatorBuilder
                .addRule("stepIdValid", StepRunReport::isStepIdValid, "StepId must be set and not empty")
                .addRule("typeValid", StepRunReport::isTypeValid, "Type must be set and not empty")
                .addRule("resultValid", StepRunReport::isResultValid, "Result must be set")
                .addMember("outputs", StepRunReport::getOutputs, Item.class);
    }
}
