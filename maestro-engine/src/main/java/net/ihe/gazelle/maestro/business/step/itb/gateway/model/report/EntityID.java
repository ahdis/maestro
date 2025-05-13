package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;

/**
 * Model for a TM report identifier
 */
public class EntityID implements Serializable {
    private String id;

    private String version;

    public EntityID() {
    }

    public EntityID(String id) {
        this.id = id;
    }

    public EntityID(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
