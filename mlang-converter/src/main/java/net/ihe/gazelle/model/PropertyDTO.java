package net.ihe.gazelle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonGetter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyDTO {
    private String key;
    private String value;

    // Getters et Setters
    @JsonGetter("key")
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    @JsonGetter("value")
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}