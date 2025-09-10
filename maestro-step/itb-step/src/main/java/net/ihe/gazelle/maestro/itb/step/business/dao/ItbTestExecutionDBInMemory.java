/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.maestro.itb.step.business.dao;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ItbTestExecutionDBInMemory {


    private static final Map<String, CompletableFuture<StepRunReportBuilder>> STEP_RUN_REPORT_FUTURE_MAP = new HashMap<>();

    private ItbTestExecutionDBInMemory() {
        // Private constructor to prevent instantiation
    }

    public static CompletableFuture<StepRunReportBuilder> getStepRunReportFuture(String sessionId) {
        return STEP_RUN_REPORT_FUTURE_MAP.get(sessionId);
    }

    public static void addStepRunReportFuture(String sessionId, CompletableFuture<StepRunReportBuilder> future) {
        STEP_RUN_REPORT_FUTURE_MAP.put(sessionId, future);
    }

    public static void releaseSession(String sessionId) {
        STEP_RUN_REPORT_FUTURE_MAP.remove(sessionId);
    }

}
