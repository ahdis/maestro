package net.ihe.gazelle.maestro.technical.jobs;

import net.ihe.gazelle.maestro.technical.jobs.testrunevents.TestRunEvent;

public interface TestRunEventListener {

    void receiveTestRun(TestRunEvent event);

}
