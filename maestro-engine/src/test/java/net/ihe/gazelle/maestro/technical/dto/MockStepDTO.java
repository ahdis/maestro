package net.ihe.gazelle.maestro.technical.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.technical.dto.testrun.StepDTO;

@JsonTypeName(MockStep.TYPE)
public class MockStepDTO extends StepDTO<MockStep> {

    public MockStepDTO() {
        super(new MockStep());
    }

    public MockStepDTO(MockStep mockStep) {
        super(mockStep);
    }

    @Override
    public Class<MockStep> getBusinessObjectClass() {
        return MockStep.class;
    }
}
