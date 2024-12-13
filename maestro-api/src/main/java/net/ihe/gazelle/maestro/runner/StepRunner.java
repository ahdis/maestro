package net.ihe.gazelle.maestro.runner;

import net.ihe.gazelle.testreport.StepRunReportBuilder;

public interface StepRunner {

    /**
     * @param stepRun contain step to run
     * @return report builder with information to build report of step execution
     */
    StepRunReportBuilder run(StepRun stepRun);

    /**
     * @return Canonical name of step that stepRunner can manage
     */
    String getSupportedStep();
}
