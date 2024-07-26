package net.ihe.gazelle;

import net.ihe.gazelle.business.exception.MissingArgumentException;
import net.ihe.gazelle.dao.InputImpl;
import net.ihe.gazelle.business.test.TestImpl;
import net.ihe.gazelle.business.test.TestRunImpl;
import net.ihe.gazelle.business.engine.EngineImpl;
import net.ihe.gazelle.business.step.validation.ValidationStep;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TestRunControllerTest {


    @org.junit.jupiter.api.Test
    void reportValidationStep() throws MissingArgumentException {
        TestRunImpl testRunImpl = new TestRunImpl();

        TestImpl test = new TestImpl();
                test.addStep(new ValidationStep("iti78-request-validation", "matchbox", "Parameters", "iti78-request-validation"))
                ;

        testRunImpl.setTest(test);

        testRunImpl
                .addInput(new InputImpl("iti78-request-validation", "chazorfuizeoifrj"))
                .addInput(new InputImpl("iti78-response-validation", "azepaoznfvhil zeb"))
                .addInput(new InputImpl("matchbox", "https://gazelle.ihe.net/matchboxv3/gazelle/validation"))
        ;


        EngineImpl engineImpl = new EngineImpl();
        engineImpl.run(testRunImpl);
        Map<String, Object> reports = engineImpl.getReports();

        Object request = reports.get("iti78-request-validation");

        assertNotNull(request);

    }

    // test: verify step is ordered


}
