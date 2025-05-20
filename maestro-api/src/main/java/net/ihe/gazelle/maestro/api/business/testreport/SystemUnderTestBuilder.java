package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.SystemUnderTestValidator;

import java.util.ArrayList;
import java.util.List;

public class SystemUnderTestBuilder extends AbstractBuilder<SystemUnderTest> {

    private EntityIdentificationBuilder systemIdentification;
    private List<String> macAddresses;
    private List<String> ipAddresses;
    private List<String> hostNames;

    public SystemUnderTestBuilder() {
        super();
        macAddresses = new ArrayList<>();
        ipAddresses = new ArrayList<>();
        hostNames = new ArrayList<>();
    }

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
    protected AbstractValidator<SystemUnderTest> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new SystemUnderTestValidator(validatorBuilderFactory);
    }

    @Override
    protected SystemUnderTest instantiate() {
        return new SystemUnderTest();
    }

    @Override
    protected void make(SystemUnderTest systemUnderTest) {
        systemUnderTest
                .setSystemIdentification(AbstractBuilder.staticBuildWithoutValidation(systemIdentification))
                .setMacAddresses(macAddresses)
                .setIpAddresses(ipAddresses)
                .setHostNames(hostNames);
    }
}
