package net.ihe.gazelle.maestro.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;

@JsonTypeName(ValidationStep.TYPE)
public class ValidationStepDTO extends StepDTO<ValidationStep> {

    public ValidationStepDTO() {
        super(new ValidationStep());
    }

    public ValidationStepDTO(ValidationStep validationStep) {
        super(validationStep);
    }

    @Override
    public Class<ValidationStep> getBusinessObjectClass() {
        return ValidationStep.class;
    }
}
