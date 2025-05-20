package net.ihe.gazelle.maestro.api.business.testrun.runner;

import net.ihe.gazelle.maestro.api.business.testrun.Step;

/**
 * A structure containing infos to manage its execution
 * @param testRunId id to step's testRun
 * @param step the step to execute
 */
public record StepRun(String testRunId, Step step) {
}
