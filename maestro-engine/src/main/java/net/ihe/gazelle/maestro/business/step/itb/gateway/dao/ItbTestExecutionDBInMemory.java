package net.ihe.gazelle.maestro.business.step.itb.gateway.dao;

import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;

import java.util.HashMap;
import java.util.Map;

public class ItbTestExecutionDBInMemory {

    private static Map<String, StepRun> sessions = new HashMap<>();

    public static void writeSessions(String sessionId, StepRun stepRun) {
        sessions.put(sessionId, stepRun);
    }

    public static void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static StepRun getStepRun(String testID) {
        return sessions.get(testID);
    }
}
