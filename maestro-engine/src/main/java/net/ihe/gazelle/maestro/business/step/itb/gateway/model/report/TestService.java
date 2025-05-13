package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;

/**
 * Model for a service executing the test suite
 */
public class TestService implements Serializable {
    private EntityID serviceID;

    private Text disclaimer;

    public TestService() {
        this.serviceID = new EntityID("Test service");
        this.disclaimer = new Text();
    }

    public TestService(EntityID serviceID, Text disclaimer) {
        this.serviceID = serviceID;
        this.disclaimer = disclaimer;
    }

    public EntityID getServiceID() {
        return serviceID;
    }

    public void setServiceID(EntityID serviceID) {
        this.serviceID = serviceID;
    }

    public Text getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(Text disclaimer) {
        this.disclaimer = disclaimer;
    }

}
