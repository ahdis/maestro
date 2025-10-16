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

package net.ihe.gazelle.maestro.engine.business.context;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.Test;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Tracks iteration over the ordered list of {@link Step} instances of a test run.
 */
public final class StepCursor {

    private final List<Step> steps;
    private int nextIndex;

    public StepCursor(List<Step> steps) {
        this.steps = List.copyOf(steps);
        this.nextIndex = 0;
    }

    public static StepCursor empty() {
        return new StepCursor(List.of());
    }

    public static StepCursor from(Test test) {
        if (test == null) {
            return empty();
        }
        return new StepCursor(test.getSteps());
    }

    public boolean hasNext() {
        return nextIndex < steps.size();
    }

    public Step next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more steps to execute");
        }
        return steps.get(nextIndex++);
    }

    public boolean hasExecutedSteps() {
        return nextIndex > 0;
    }

    public Step current() {
        if (!hasExecutedSteps()) {
            throw new IllegalStateException("No step has been executed yet");
        }
        return steps.get(nextIndex - 1);
    }
}

