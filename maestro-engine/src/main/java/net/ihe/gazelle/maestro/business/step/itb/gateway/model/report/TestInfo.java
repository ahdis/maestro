package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;

/**
 * Model for the informations about a test entity
 */
public class TestInfo implements Serializable {
    private String name;

    private String version;

    private Text description;

    public TestInfo() {
    }

    public TestInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public TestInfo(String name, String version, Text description) {
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }
}
