package net.ihe.gazelle.maestro.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.business.step.assertion.AssertionStep;

@JsonTypeName(AssertionStep.TYPE)
public class AssertionStepDTO extends StepDTO<AssertionStep> {

    public AssertionStepDTO() {
        super(new AssertionStep());
    }

    public AssertionStepDTO(AssertionStep assertionStep) {
        super(assertionStep);
    }

    @Override
    public Class<AssertionStep> getBusinessObjectClass() {
        return AssertionStep.class;
    }
}
