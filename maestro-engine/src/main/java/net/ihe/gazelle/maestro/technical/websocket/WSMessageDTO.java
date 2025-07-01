package net.ihe.gazelle.maestro.technical.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WSMessageDTO<T> {

    private String testSetId;
    private String type;
    private T content;

    public WSMessageDTO() {
    }

    public WSMessageDTO(String testSetId, String type, T content) {
        this.testSetId = testSetId;
        this.type = type;
        this.content = content;
    }

    public WSMessageDTO(WSMessageDTO<T> wsMessage) {
        this.testSetId = wsMessage.testSetId;
        this.type = wsMessage.type;
        this.content = wsMessage.content;
    }

    @JsonProperty("testSetId")
    public String getTestRunId() {
        return testSetId;
    }

    public void setTestRunId(String testRunId) {
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
    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
