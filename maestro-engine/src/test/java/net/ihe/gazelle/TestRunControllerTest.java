package net.ihe.gazelle;

import net.ihe.gazelle.maestro.api.business.testrun.*;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.business.engine.MaestroImpl;
import net.ihe.gazelle.maestro.technical.step.validation.GazelleValidationServiceHandler;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.maestro.technical.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.technical.handler.HandlerProvider;
import net.ihe.gazelle.maestro.technical.jobs.MicroprofileMaestroProducer;
import net.ihe.gazelle.maestro.technical.jobs.StepMockEmitter;
import net.ihe.gazelle.maestro.technical.jobs.TestRunMockEmitter;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

class TestRunControllerTest {


    @org.junit.jupiter.api.Test
    void reportValidationStep() throws MissingArgumentException {
        TestRun testRun = new TestRun();

        Test test = new Test();
                test.addStep(new ValidationStep(
                        "iti78-request-validation",
                        new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                        new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                        new PropertyRef<>(ValidationStep.CONTENT_TO_VALIDATE, "iti78-request", byte[].class)));

        testRun.setTest(test);

        testRun
                .addInputs(new Property<>("iti78-request", "eaozieuaozieuaozieauo".getBytes(StandardCharsets.UTF_8)))
                .addInputs(new Property<>("matchbox", "https://gazelle.ihe.net/matchboxv3/gazelle/validation"))
        ;


        Maestro engine = getEngine();
        engine.run(testRun);

    }

    private Maestro getEngine() {
        return new MaestroImpl(new MicroprofileMaestroProducer(new TestRunMockEmitter(), new StepMockEmitter()), new SessionStoreContextImpl());
    }

    // test: verify testreport is ordered
    static class HandlerProviderMock implements HandlerProvider {
        @Override
        public Handler getHandler(Class<? extends Handler> handlerType, String serviceName) {
            return new ValidationHandlerMock();
        }
    }

    static class ValidationHandlerMock extends GazelleValidationServiceHandler {


        public ValidationHandlerMock() {
            /*Empty constructor used for mocking*/
        }

        @Override
        public boolean isAvailable() {
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
