package net.ihe.gazelle.maestro.validation.step.technical.record;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissector;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissectorFactory;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;

public class ValidationStepOutputDissectorFactory implements StepOutputDissectorFactory {

    @Override
    public boolean supports(StepRunReport stepRunReport) {
        return ValidationStepDefinition.TYPE.equals(stepRunReport.getType());
    }

    @Override
    public StepOutputDissector create() {
        return new ValidationStepOutputDissector();
    }
}
