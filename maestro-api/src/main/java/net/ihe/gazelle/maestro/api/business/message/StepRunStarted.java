package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Event;

@Event
public record StepRunStarted(String suiteRunId, String testRunId, String stepRunId) implements Message {
}
