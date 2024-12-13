package net.ihe.gazelle.maestro.runner;

import net.ihe.gazelle.testreport.StepRunReportBuilder;

/**
 * Step message used by a producer to notify listener that a step execution ended.
 * It contains a report builder loaded with infos to build executed step's report
 */
public class FinishedStepEvent extends StepMessage {

    private final StepRunReportBuilder stepRunReport;

    public FinishedStepEvent(StepRunReportBuilder stepRunReport, StepRun stepRun) {
        super(stepRun);
        this.stepRunReport = stepRunReport;
    }

    public StepRunReportBuilder getStepRunReport() {
        return stepRunReport;
    }

}
