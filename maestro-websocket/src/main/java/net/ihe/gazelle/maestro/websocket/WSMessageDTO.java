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

package net.ihe.gazelle.maestro.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WSMessageDTO {

    private String testSetId;
    private String type;
    private String content;

    public WSMessageDTO() {
    }

    public WSMessageDTO(String testSetId, String type, String content) {
        this.testSetId = testSetId;
        this.type = type;
        this.content = content;
    }

    public WSMessageDTO(WSMessageDTO wsMessage) {
        this.testSetId = wsMessage.testSetId;
        this.type = wsMessage.type;
        this.content = wsMessage.content;
    }

    @JsonProperty("testSetId")
    public String getTestSetId() {
        return testSetId;
    }

    public void setTestSetId(String testRunId) {
        this.testSetId = testRunId;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
