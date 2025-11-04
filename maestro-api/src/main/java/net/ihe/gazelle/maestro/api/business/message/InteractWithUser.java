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

public class InteractWithUser implements Message {

    private String interactionTitle;
    private String message;

    public InteractWithUser() {
        // For serialization
    }

    public InteractWithUser(String interactionTitle, String message) {
        this.interactionTitle = interactionTitle;
        this.message = message;
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
}
