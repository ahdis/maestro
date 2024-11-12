package net.ihe.gazelle.report.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Business rules
 * name is mandatory
 * content xor reference
 */
public class Item {

    private String name;
    private byte[] content;
    private String reference;

    public Item() {
    }

    public Item(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public Item(String name, String reference) {
        this.name = name;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Item setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public Item setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public boolean isNameValid() {
        return name != null && !name.isBlank();
    }

    public boolean isContentValid() {
        if (content != null && content.length > 0) {
            return reference == null;
        } else {
            return reference != null;
        }
    }

    public boolean isReferenceValid() {
        if (reference != null && !reference.isBlank()) {
            return content == null;
        } else {
            return content != null;
        }
    }

    public boolean isValid() {
        return isNameValid() && isContentValid() && isReferenceValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(name, item.name)
                && Objects.deepEquals(content, item.content)
                && Objects.equals(reference, item.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(content), reference);
    }
}
