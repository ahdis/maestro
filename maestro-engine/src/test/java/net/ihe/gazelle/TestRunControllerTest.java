package net.ihe.gazelle;

import net.ihe.gazelle.maestro.business.engine.MaestroImpl;
import net.ihe.gazelle.maestro.PropertyRef;
import net.ihe.gazelle.maestro.business.step.validation.ValidationHandler;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.maestro.Maestro;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.maestro.technical.handler.HandlerProvider;
import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.maestro.technical.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.technical.jobs.MicroprofileMaestroProducer;
import net.ihe.gazelle.maestro.technical.jobs.StepMockEmitter;
import net.ihe.gazelle.maestro.technical.jobs.TestRunMockEmitter;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;
import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Test;
import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.validation.api.domain.metadata.structure.ValidationProfile;
import net.ihe.gazelle.validation.api.domain.report.structure.ValidationReport;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;

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
                        new PropertyRef<>(ValidationStep.CONTENT_TO_VALIDATE, "iti78-request")));

        testRun.setTest(test);

        testRun
                .addInputs(new Property<>("iti78-request", "eaozieuaozieuaozieauo".getBytes(StandardCharsets.UTF_8)))
                .addInputs(new Property<>("matchbox", "https://gazelle.ihe.net/matchboxv3/gazelle/validation"))
        ;


        Maestro engine = getEngine();
        engine.run(testRun);

    }

    private Maestro getEngine() {
        return new MaestroImpl(new BValidatorBuilderFactory(), new MicroprofileMaestroProducer(new TestRunMockEmitter(), new StepMockEmitter()), new SessionStoreContextImpl());
    }

    // test: verify testreport is ordered
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
