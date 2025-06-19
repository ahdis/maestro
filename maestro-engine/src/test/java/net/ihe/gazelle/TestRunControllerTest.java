package net.ihe.gazelle;

import net.ihe.gazelle.maestro.api.business.testrun.*;
import net.ihe.gazelle.maestro.business.engine.MaestroImpl;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.maestro.technical.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.technical.jobs.EventMaestroProducer;
import net.ihe.gazelle.maestro.technical.jobs.StepMockEvent;
import net.ihe.gazelle.maestro.technical.jobs.TestRunMockEvent;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;

import java.nio.charset.StandardCharsets;

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
        return new MaestroImpl(new EventMaestroProducer(new TestRunMockEvent(), new StepMockEvent()), new SessionStoreContextImpl());
    }
}
