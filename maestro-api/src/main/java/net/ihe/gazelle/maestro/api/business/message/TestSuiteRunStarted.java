package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Event;

@Event
public record TestSuiteRunStarted(String testSuiteRunId) implements Message {
}
