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

import net.ihe.gazelle.maestro.api.business.test.SupportedInput;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class InteractWithUser implements Message {

    @Serial
    private static final long serialVersionUID = 5192092115743420623L;

    private String interactionTitle;
    private String message;
    private Long timeout;
    private final List<SupportedInput> supportedInputs;

    public InteractWithUser() {
        this(null, null, null);
    }

    public InteractWithUser(String interactionTitle, String message, Long timeout) {
        this.interactionTitle = interactionTitle;
        this.message = message;
        this.timeout = timeout;
        this.supportedInputs = new ArrayList<>();
    }

    public String getInteractionTitle() {
        return interactionTitle;
    }

    public InteractWithUser setInteractionTitle(String interactionTitle) {
        this.interactionTitle = interactionTitle;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public InteractWithUser setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getTimeout() {
        return timeout;
    }

    public InteractWithUser setTimeout(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    public List<SupportedInput> getSupportedInputs() {
        return new ArrayList<>(supportedInputs);
    }

    public InteractWithUser addSupportedInput(SupportedInput supportedInput) {
        this.supportedInputs.add(supportedInput);
        return this;
    }

    public InteractWithUser addSupportedInputs(List<SupportedInput> supportedInputs) {
        this.supportedInputs.addAll(supportedInputs);
        return this;
    }
}
