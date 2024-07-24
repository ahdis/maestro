package net.ihe.gazelle;

import net.ihe.gazelle.business.exception.MissingArgumentException;
import net.ihe.gazelle.dao.Input;
import net.ihe.gazelle.business.test.Test;
import net.ihe.gazelle.business.test.TestRun;
import net.ihe.gazelle.business.engine.TestEngine;
import net.ihe.gazelle.business.step.validation.ValidationStep;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TestRunControllerTest {


    @org.junit.jupiter.api.Test
    void reportValidationStep() throws MissingArgumentException {
        TestRun testRun = new TestRun();

        Test test = new Test()
                .addStep(new ValidationStep("iti78-request-validation", "matchbox", "Parameters", "iti78-request-validation"))
                ;

        testRun.setTest(test);

        testRun
                .addInput(new Input("iti78-request-validation", "chazorfuizeoifrj"))
                .addInput(new Input("iti78-response-validation", "azepaoznfvhil zeb"))
                .addInput(new Input("matchbox", "https://gazelle.ihe.net/matchboxv3/gazelle/validation"))
        ;


        TestEngine testEngine = new TestEngine();
        testEngine.run(testRun);
        Map<String, Object> reports = testEngine.getReports();

        Object request = reports.get("iti78-request-validation");

        assertNotNull(request);

    }

    // test: verify step is ordered


}
