package net.ihe.gazelle.maestro.technical.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WSMessageDTO<T> {

    private String testRunId;
    private String type;
    private T content;

    public WSMessageDTO() {
    }

    public WSMessageDTO(String testRunId, String type, T content) {
        this.testRunId = testRunId;
        this.type = type;
        this.content = content;
    }

    public WSMessageDTO(WSMessageDTO<T> wsMessage) {
        this.testRunId = wsMessage.testRunId;
        this.type = wsMessage.type;
        this.content = wsMessage.content;
    }

    @JsonProperty("testRunId")
    public String getTestRunId() {
        return testRunId;
    }

    public void setTestRunId(String testRunId) {
        this.testRunId = testRunId;
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
