package net.ihe.gazelle.maestro.api.business.testreport;

public class Input {


    private String name;
    private byte[] content;
    private String reference;

    public Input() {
    }

    public Input(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public Input(String name, String reference) {
        this.name = name;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public Input setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Input setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public Input setReference(String reference) {
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
}
