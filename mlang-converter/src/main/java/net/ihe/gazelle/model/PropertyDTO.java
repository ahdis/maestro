package net.ihe.gazelle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonGetter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyDTO {
    private String name;
    private String type;
    private String value;

    // Getters et Setters
    @JsonGetter("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonGetter("type")
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @JsonGetter("value")
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}