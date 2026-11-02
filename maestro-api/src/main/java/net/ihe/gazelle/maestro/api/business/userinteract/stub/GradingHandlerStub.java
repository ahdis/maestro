/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.userinteract.stub;

import net.ihe.gazelle.maestro.api.business.message.GradingCompleted;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.userinteract.GradingHandler;

import java.util.List;

/**
 * Stub used for tests
 */
public class GradingHandlerStub implements GradingHandler {

    /**
     * Default constructor
     */
    public GradingHandlerStub() {
        // Empty
    }

    @Override
    public GradingCompleted displayMessage(List<String> criterion, long timeout) {
        return new GradingCompleted().setResult(StepResult.PASSED);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
