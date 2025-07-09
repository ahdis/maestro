/*
 * Copyright 2025 IHE International.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
