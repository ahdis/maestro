package net.ihe.gazelle.report.model;

import java.util.Objects;

/**
 * Business rules
 * id is mandatory
 */
public class Test {

    private String id;
    private String name;
    private String version;
    private String description;

    public Test() {
    }

    public Test(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Test setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Test setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Test setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Test setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isIdValid() {
        return id != null && !id.isBlank();
    }

    public boolean isNameValid() {
        return name == null || !name.isBlank();
    }

    public boolean isVersionValid() {
        return version == null || !version.isBlank();
    }

    public boolean isDescriptionValid() {
        return description == null || !description.isBlank();
    }

    public boolean isValid() {
        return isIdValid()
                && isNameValid()
                && isVersionValid()
                && isDescriptionValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test test)) return false;
        return Objects.equals(id, test.id)
                && Objects.equals(name, test.name)
                && Objects.equals(version, test.version)
                && Objects.equals(description, test.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, version, description);
    }
}
