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
import java.util.NoSuchElementException;

/**
 * Tracks iteration over the ordered list of {@link TestRun} instances within a test set.
 */
public final class TestRunCursor {

    private final List<TestRun> testRuns;
    private int nextIndex;

    public TestRunCursor(List<TestRun> testRuns) {
        this.testRuns = List.copyOf(testRuns == null ? List.of() : testRuns);
        this.nextIndex = 0;
    }

    public static TestRunCursor empty() {
        return new TestRunCursor(List.of());
    }

    public static TestRunCursor from(TestSetRun testSetRun) {
        if (testSetRun == null) {
            return empty();
        }
        return new TestRunCursor(testSetRun.getTestRuns());
    }

    public boolean hasNext() {
        return nextIndex < testRuns.size();
    }

    public TestRun next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more test runs to execute");
        }
        return testRuns.get(nextIndex++);
    }

    public boolean hasExecutedRuns() {
        return nextIndex > 0;
    }

    public TestRun current() {
        if (!hasExecutedRuns()) {
            throw new IllegalStateException("No test run has been executed yet");
        }
        return testRuns.get(nextIndex - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestRunCursor cursor)) {
            return false;
        }
        return testRuns.equals(cursor.testRuns) && nextIndex == cursor.nextIndex;
    }

    @Override
    public int hashCode() {
        return 31 * testRuns.hashCode() + nextIndex;
    }
}
