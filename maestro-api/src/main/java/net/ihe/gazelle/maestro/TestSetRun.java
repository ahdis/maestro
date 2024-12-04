package net.ihe.gazelle.maestro;

import java.util.List;

/**
 * A TestSetRun is a structure containing several TestRun that will be executed iteratively.
 * @see TestRun
 * @param testSetId an id that will be spread to each element of testRuns
 * @param testRuns the list of testRuns to execute
 */
public record TestSetRun(String testSetId, List<TestRun> testRuns) {
}
