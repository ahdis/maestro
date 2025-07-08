package net.ihe.gazelle.maestro.api.business.testrun.runner;

import net.ihe.gazelle.maestro.api.business.testrun.Step;

/**
 * A structure containing infos to manage its execution
 * @param testSetId id to step's testSet
 * @param testRunId id to step's testRun
 * @param step the step to execute
 */
public record StepRun(String testSetId, String testRunId, Step step) {
}
