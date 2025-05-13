package net.ihe.gazelle.maestro.context;

import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.testreport.StepRunReportBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRunSessionStoreTest {

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

}
