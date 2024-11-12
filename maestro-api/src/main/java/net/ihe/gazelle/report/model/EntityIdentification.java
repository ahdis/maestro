package net.ihe.gazelle.report.model;

import java.util.Objects;

/**
 * Business rules
 * name is mandatory
 */
public class EntityIdentification {

    private String version;
    private String name;

    public EntityIdentification() {
    }

    public EntityIdentification(String name) {
        this.name = name;
    }

    public EntityIdentification(String version, String name) {
        this(name);
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public EntityIdentification setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getName() {
        return name;
    }

    public EntityIdentification setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isVersionValid() {
        return version == null || !version.isBlank();
    }

    public boolean isNameValid() {
        return name != null && !name.isBlank();
    }

    public boolean isValid() {
        return isVersionValid() && isNameValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityIdentification that)) return false;
        return Objects.equals(version, that.version)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, name);
    }
}
