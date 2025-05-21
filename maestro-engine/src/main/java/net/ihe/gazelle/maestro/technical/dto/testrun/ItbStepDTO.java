package net.ihe.gazelle.maestro.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.business.step.assertion.AssertionStep;
import net.ihe.gazelle.maestro.business.step.itb.ItbStep;

@JsonTypeName(ItbStep.TYPE)
public class ItbStepDTO extends StepDTO<AssertionStep> {

    public ItbStepDTO() {
        super(new AssertionStep());
    }

    public ItbStepDTO(AssertionStep assertionStep) {
        super(assertionStep);
    }

    @Override
    public Class<AssertionStep> getBusinessObjectClass() {
        return AssertionStep.class;
    }
}
