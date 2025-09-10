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

package net.ihe.gazelle.maestro.business;

import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;

import java.util.List;

public class TestSetValidator {

    public void validate(TestSetRun testSetRun) throws IllegalArgumentException{
        List<TestRun> testRuns = testSetRun.testRuns();
        for (TestRun testRun : testRuns) {
            if(testRun == null) {
                throw new IllegalArgumentException("TestSet " + testSetRun.testSetId() + " contains at least one null testRun");
            }
        }
    }

}
