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

package net.ihe.gazelle.maestro.api.business.message;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a message for asking grading.
 */
public class AskGrading implements Message {

    @Serial
    private static final long serialVersionUID = -2433841356039722806L;

    /**
     * Criteria to display to the user.
     */
    private List<String> criteria;

    /**
     * Default constructor.
     */
    public AskGrading() {
        criteria = new ArrayList<>();
    }

    /**
     * Retrieves the criterion of the interaction.
     *
     * @return the criterion as a string, or null if no criterion is set.
     */
    public List<String> getCriteria() {
        return new ArrayList<>(criteria);
    }

    /**
     * Sets the criterion for the interaction.
     *
     * @param criteria the criterion to be set as a string.
     * @return the current instance of {@code AskGrading} for method chaining.
     */
    public AskGrading setCriteria(List<String> criteria) {
        this.criteria = new ArrayList<>(criteria);
        return this;
    }
}
