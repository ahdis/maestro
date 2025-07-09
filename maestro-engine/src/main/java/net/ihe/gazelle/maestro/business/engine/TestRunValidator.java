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

package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.api.business.testrun.DuplicatedStepsException;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;

import java.util.ArrayList;
import java.util.List;

public class TestRunValidator {
    public void validate(TestRun testRun) throws MissingArgumentException {
        if (testRun == null) {
            throw new MissingArgumentException("argument testRun is null");
        }
        if (testRun.getTest() == null) {
            throw new MissingArgumentException("argument test is null");
        }
        if (testRun.getTest().getSteps() == null || testRun.getTest().getSteps().isEmpty()) {
            throw new MissingArgumentException("argument steps is null or empty");
        }
        allStepsUnique(testRun.getTest().getSteps());
    }

    private void allStepsUnique(List<Step> steps) {
        List<String> names = new ArrayList<>();
        for (Step step : steps) {
            if (!names.contains(step.getName())) {
                names.add(step.getName());
            } else {
                throw new DuplicatedStepsException("Several steps has the same name : " + step.getName() + " should be unique");
            }
        }
    }
}
