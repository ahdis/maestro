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

package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.runner.AsyncStepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.itb.step.business.dao.ItbTestExecutionDBInMemory;

public class ItbStepRunner extends AsyncStepRunner {

    private final ItbHandler handler;

    public ItbStepRunner(ItbHandler itbHandler) {
        this.handler = itbHandler;
    }

    @Override
    public void runAsync(StepRun stepRun) {
        String sessionId = handler.handle((ItbStep) stepRun.step());
        ItbTestExecutionDBInMemory.writeSessions(sessionId, stepRun);
    }

    @Override
    public final String getSupportedStep() {
        return ItbStep.TYPE;
    }

}
