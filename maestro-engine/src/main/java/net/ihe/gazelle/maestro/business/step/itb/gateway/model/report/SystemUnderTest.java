package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;
import java.util.List;

/**
 * Model for the system under test
 */
public class SystemUnderTest implements Serializable {
    private EntityID systemID;

    private List<String> macAddresses;

    private List<String> ipAddresses;

    private List<String> hostNames;

    public SystemUnderTest() {
        this.systemID = new EntityID("SUT");
    }

    public SystemUnderTest(EntityID systemID) {
        this.systemID = systemID;
    }

    public SystemUnderTest(EntityID systemID, List<String> macAddresses, List<String> ipAddresses, List<String> hostNames) {
        this.systemID = systemID;
        this.macAddresses = macAddresses;
        this.ipAddresses = ipAddresses;
        this.hostNames = hostNames;
    }

    public EntityID getSystemID() {
        return systemID;
    }

    public void setSystemID(EntityID systemID) {
        this.systemID = systemID;
    }

    public List<String> getMacAddresses() {
        return macAddresses;
    }

    public void setMacAddresses(List<String> macAddresses) {
        this.macAddresses = macAddresses;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public List<String> getHostNames() {
        return hostNames;
    }

    public void setHostNames(List<String> hostNames) {
        this.hostNames = hostNames;
    }
}
