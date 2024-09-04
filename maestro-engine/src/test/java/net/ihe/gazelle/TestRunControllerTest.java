package net.ihe.gazelle;

import net.ihe.gazelle.business.step.validation.ValidationHandler;
import net.ihe.gazelle.engine.Engine;
import net.ihe.gazelle.handler.HandlerProvider;
import net.ihe.gazelle.runner.Handler;
import net.ihe.gazelle.technical.StepRunnerProviderSPI;
import net.ihe.gazelle.technical.ws.MissingArgumentException;
import net.ihe.gazelle.technical.dao.InputImpl;
import net.ihe.gazelle.business.test.TestImpl;
import net.ihe.gazelle.business.test.TestRunImpl;
import net.ihe.gazelle.business.engine.EngineImpl;
import net.ihe.gazelle.business.step.validation.ValidationStep;
import net.ihe.gazelle.validation.api.domain.metadata.structure.ValidationProfile;
import net.ihe.gazelle.validation.api.domain.report.structure.ValidationReport;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TestRunControllerTest {


    @org.junit.jupiter.api.Test
    void reportValidationStep() throws MissingArgumentException {
        TestRunImpl testRunImpl = new TestRunImpl();

        Map<String, String> properties = new HashMap<>();
        properties.put(ValidationStep.VALIDATION_SERVICE, "matchbox");
        properties.put(ValidationStep.VALIDATION_PROFILE, "Parameters");
        properties.put(ValidationStep.CONTENT_TO_VALIDATE, "${iti78-request-validation}");

        TestImpl test = new TestImpl();
                test.addStep(new ValidationStep("iti78-request-validation", properties));

        testRunImpl.setTest(test);

        testRunImpl
                .addInput(new InputImpl("${iti78-request-validation}", "chazorfuizeoifrj"))
                .addInput(new InputImpl("matchbox", "https://gazelle.ihe.net/matchboxv3/gazelle/validation"))
        ;


        Engine engine = getEngine();
        engine.run(testRunImpl);
        Map<String, Object> reports = engine.getReports();

        Object request = reports.get("iti78-request-validation");

        assertNotNull(request);

    }

    private Engine getEngine() {
        return new EngineImpl(new StepRunnerProviderSPI(new HandlerProviderMock()));
    }

    // test: verify step is ordered
    static class HandlerProviderMock implements HandlerProvider {
        @Override
        public Handler getHandler(Class<? extends Handler> handlerType, String serviceName) {
            return new ValidationHandlerMock();
        }
    }

    static class ValidationHandlerMock extends ValidationHandler {


        public ValidationHandlerMock() {
            /*Empty constructor used for mocking*/
        }

        @Override
        public boolean isAvailable(String service) {
            return true;
        }

        @Override
        public ValidationReport validate(ValidationRequest validationRequest) {
            return new ValidationReport();
        }

        @Override
        public List<ValidationProfile> getValidationProfiles() {
            return List.of();
        }
    }

}
