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

package net.ihe.gazelle.maestro.api.business.testrun;

import java.util.List;

/**
 * A TestSetRun is a structure containing several TestRun that will be executed iteratively.
 * {@link TestRun}
 * @param testSetId an id that will be spread to each element of testRuns
 * @param testRuns the list of testRuns to execute
 */
public record TestSetRun(String testSetId, List<TestRun> testRuns) {
}
