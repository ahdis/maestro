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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A structure contains in TestRun defining TestRun's steps.
 * <p>
 *     - <b>id</b> TestRun id
 *     - <b>name</b> a string of test name
 *     - <b>steps</b> a list of step
 * </p>
 * {@link Step}
 */
public class Test {

    private String id;
    private String name;
    private List<Step> steps = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Step> getSteps() {
        return new ArrayList<>(steps);
    }

    public Test setSteps(List<Step> steps) {
        this.steps = new ArrayList<>(steps);
        return this;
    }

    public Test addStep(Step step) {
        steps.add(step);
        return this;
    }

    public boolean isIdValid() {
        return id != null && !id.isBlank();
    }

    public boolean isNameValid() {
        return name != null && !name.isBlank();
    }

    public boolean areStepsValid() {
        return steps != null && !steps.isEmpty();
    }

    public boolean areStepsUnique() {
        List<String> names = new ArrayList<>();
        for (Step step : steps) {
            if (!names.contains(step.getName())) {
                names.add(step.getName());
            } else {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Test test)) return false;
        return Objects.equals(id, test.id)
                && Objects.equals(name, test.name)
                && Objects.equals(steps, test.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, steps);
    }
}

