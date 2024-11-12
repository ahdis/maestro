package net.ihe.gazelle.report.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.Item;
import net.ihe.gazelle.report.model.StepRun;

public class StepRunValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<StepRun> {

    public StepRunValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends StepRun> getType() {
        return StepRun.class;
    }

    @Override
    protected ValidatorBuilder<StepRun> supplyRulesAndMembers(ValidatorBuilder<StepRun> validatorBuilder) {
        return validatorBuilder
                .addRule("stepIdValid", StepRun::isStepIdValid, "StepId must be set and not empty")
                .addRule("typeValid", StepRun::isTypeValid, "Type must be set and not empty")
                .addRule("resultValid", StepRun::isResultValid, "Result must be set")
                .addMember("outputs", StepRun::getOutputs, Item.class);
    }
}
