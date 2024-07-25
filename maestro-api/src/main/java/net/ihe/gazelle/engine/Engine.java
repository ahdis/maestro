package net.ihe.gazelle.engine;

import net.ihe.gazelle.test.TestRun;

import java.util.Map;

public interface Engine {

    Map<String, Object> getReports();

    void run(TestRun testRun);
}
