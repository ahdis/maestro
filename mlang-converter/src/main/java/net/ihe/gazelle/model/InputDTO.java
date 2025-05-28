package net.ihe.gazelle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonGetter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InputDTO {
    private String key;
    private String label;
    private String format;
    private String type;
    private boolean required;

    // Getters et Setters
    @JsonGetter("key")
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    @JsonGetter("label")
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    @JsonGetter("format")
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    @JsonGetter("type")
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @JsonGetter("required")
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}