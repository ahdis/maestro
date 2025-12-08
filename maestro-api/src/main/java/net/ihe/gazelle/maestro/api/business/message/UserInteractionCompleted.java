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

package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.maestro.api.business.property.Property;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class UserInteractionCompleted implements Message {

    @Serial
    private static final long serialVersionUID = 8043799892319471672L;

    private String sessionId;
    private final List<Property> inputs;

    public UserInteractionCompleted() {
        this(null);
    }

    public UserInteractionCompleted(String sessionId) {
        this.sessionId = sessionId;
        this.inputs = new ArrayList<>();
    }

    public String getSessionId() {
        return sessionId;
    }

    public UserInteractionCompleted setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public List<Property> getInputs() {
        return new ArrayList<>(inputs);
    }

    public UserInteractionCompleted addInput(Property input) {
        this.inputs.add(input);
        return this;
    }

    public UserInteractionCompleted addInputs(List<Property> inputs) {
        this.inputs.addAll(inputs);
        return this;
    }
}

