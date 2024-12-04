package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.testreport.Item;

import java.util.Base64;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "content", "reference"})
public class ItemDTO {

    @JsonIgnore
    private final Item domain;

    public ItemDTO() {
        this.domain = new Item();
    }

    public ItemDTO(Item domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "name")
    public String getName() {
        return domain.getName();
    }

    @JsonSetter(value = "name")
    public void setName(String name) {
        domain.setName(name);
    }

    @JsonGetter(value = "content")
    public String getContent() {
        if (domain.getContent() == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(domain.getContent());
    }

    @JsonSetter(value = "content")
    public void setContent(String content) {
        domain.setContent(Base64.getDecoder().decode(content));
    }

    @JsonGetter(value = "reference")
    public String getReference() {
        return domain.getReference();
    }

    @JsonSetter(value = "reference")
    public void setReference(String reference) {
        domain.setReference(reference);
    }

    @JsonIgnore
    public Item getDomain() {
        return domain;
    }
}
