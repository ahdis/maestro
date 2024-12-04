package net.ihe.gazelle.maestro.runner;

import net.ihe.gazelle.maestro.Step;

/**
 * A structure containing infos to manage its execution
 * @param testRunId id to step's testRun
 * @param step the step to execute
 */
public record StepRun(String testRunId, Step step) {
}
