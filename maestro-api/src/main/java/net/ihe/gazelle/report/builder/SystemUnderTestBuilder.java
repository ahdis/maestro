package net.ihe.gazelle.report.builder;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.SystemUnderTest;
import net.ihe.gazelle.report.visitor.SystemUnderTestBuilderVisitor;

import java.util.ArrayList;
import java.util.List;

public class SystemUnderTestBuilder extends AbstractBuilder<SystemUnderTest> {

    private EntityIdentificationBuilder systemIdentification;
    private List<String> macAddresses;
    private List<String> ipAddresses;
    private List<String> hostNames;

    public SystemUnderTestBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        macAddresses = new ArrayList<>();
        ipAddresses = new ArrayList<>();
        hostNames = new ArrayList<>();
    }

    public SystemUnderTestBuilder setSystemIdentification(EntityIdentificationBuilder systemIdentification) {
        this.systemIdentification = systemIdentification;
        return this;
    }

    public SystemUnderTestBuilder setMacAddresses(List<String> macAddresses) {
        this.macAddresses = macAddresses;
        return this;
    }

    public SystemUnderTestBuilder addMacAddress(String macAddress) {
        this.macAddresses.add(macAddress);
        return this;
    }

    public SystemUnderTestBuilder addMacAddresses(List<String> macAddresses) {
        this.macAddresses.addAll(macAddresses);
        return this;
    }

    public SystemUnderTestBuilder setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
        return this;
    }

    public SystemUnderTestBuilder addIpAddress(String ipAddress) {
        this.ipAddresses.add(ipAddress);
        return this;
    }

    public SystemUnderTestBuilder addIpAddresses(List<String> ipAddresses) {
        this.ipAddresses.addAll(ipAddresses);
        return this;
    }

    public SystemUnderTestBuilder setHostNames(List<String> hostNames) {
        this.hostNames = hostNames;
        return this;
    }

    public SystemUnderTestBuilder addHostName(String hostName) {
        this.hostNames.add(hostName);
        return this;
    }

    public SystemUnderTestBuilder addHostNames(List<String> hostNames) {
        this.hostNames.addAll(hostNames);
        return this;
    }

    @Override
    protected BuilderVisitor getBuilderVisitor() {
        return SystemUnderTestBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends SystemUnderTest> getParameterClass() {
        return SystemUnderTest.class;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setSystemIdentification(AbstractBuilder.buildField(systemIdentification))
                .setMacAddresses(macAddresses)
                .setIpAddresses(ipAddresses)
                .setHostNames(hostNames);
    }
}
