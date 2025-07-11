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

package net.ihe.gazelle.maestro.technical.jobs.testrunevents;


import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

/**
 * A message to start TestRun Execution
 */
public class StartTestRunCommand implements TestRunEvent {

    private final TestRun testRun;

    public StartTestRunCommand(TestRun testRun) {
        this.testRun = testRun;
    }

    public TestRun getTestRun() {
        return testRun;
    }

}
