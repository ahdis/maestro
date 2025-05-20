package net.ihe.gazelle.maestro.api.business.testrun;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.context.Context;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestRunSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestSetSessionStore;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContextTest {

    TestRunSessionStore testRunSessionStore;

    @BeforeEach
    void setUp() {
        Context context = new Context();
        TestRun testRun = new TestRun();
        List<StepRunReportBuilder> stepRunReportBuilders = new ArrayList<>();
        testRunSessionStore = new TestRunSessionStore(context, testRun, stepRunReportBuilders);
    }

    @Test
    void setContextTest() {
        Context context = new Context();
        testRunSessionStore.setContext(context);
        assertEquals(context, testRunSessionStore.getContext());
    }

    @Test
    void setStepReportTest(){
        StepRunReportBuilder stepRunReportBuilder = new StepRunReportBuilder();
        List<StepRunReportBuilder> stepRunReportBuilders = new ArrayList<>();
        stepRunReportBuilders.add(stepRunReportBuilder);
        testRunSessionStore.setStepReports(stepRunReportBuilders);
        assertEquals(stepRunReportBuilders, testRunSessionStore.getStepReports());
        assertEquals(1, testRunSessionStore.getStepReports().size());
        assertEquals(stepRunReportBuilder, testRunSessionStore.getStepReports().getFirst());
    }

    @Test
    void testSetSessionStore() {
        UnexpectedErrorBuilder error1 = new UnexpectedErrorBuilder()
                .setName("error1")
                .setMessage("message1");
        UnexpectedErrorBuilder error2 = new UnexpectedErrorBuilder()
                .setName("error2")
                .setMessage("message2");

        TestRunReportBuilder testRunReportBuilder1 = new TestRunReportBuilder().addUnexpectedError(error1);
        TestRunReportBuilder testRunReportBuilder2 = new TestRunReportBuilder().addUnexpectedError(error2);

        EqualsVerifier.simple()
                .forClass(TestSetSessionStore.class)
                .withPrefabValues(TestRunReportBuilder.class, testRunReportBuilder1, testRunReportBuilder2)
                .verify();
    }

}
