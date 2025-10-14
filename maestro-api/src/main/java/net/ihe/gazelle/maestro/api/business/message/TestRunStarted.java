package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Event;

@Event
public record TestRunStarted(String testSuiteId, String testRunId) implements Message {
}
