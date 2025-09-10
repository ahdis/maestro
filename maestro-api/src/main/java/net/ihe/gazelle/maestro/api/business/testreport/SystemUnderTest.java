/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.api.business.testreport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Business rules <br>
 * systemIdentification mandatory
 */
public class SystemUnderTest {

    private EntityIdentification systemIdentification;
    private List<String> macAddresses;
    private List<String> ipAddresses;
    private List<String> hostNames;

    public SystemUnderTest() {
        this.macAddresses = new ArrayList<>();
        this.ipAddresses = new ArrayList<>();
        this.hostNames = new ArrayList<>();
    }

    public SystemUnderTest(EntityIdentification systemIdentification) {
        this();
        this.systemIdentification = systemIdentification;
    }

    public EntityIdentification getSystemIdentification() {
        return systemIdentification;
    }

    public SystemUnderTest setSystemIdentification(EntityIdentification systemIdentification) {
        this.systemIdentification = systemIdentification;
        return this;
    }

    public List<String> getMacAddresses() {
        return new ArrayList<>(macAddresses);
    }

    public SystemUnderTest setMacAddresses(List<String> macAddresses) {
        this.macAddresses = new ArrayList<>(macAddresses);
        return this;
    }

    public SystemUnderTest addMacAddress(String macAddress) {
        macAddresses.add(macAddress);
        return this;
    }

    public SystemUnderTest addMacAddresses(List<String> macAddresses) {
        this.macAddresses.addAll(macAddresses);
        return this;
    }

    public List<String> getIpAddresses() {
        return new ArrayList<>(ipAddresses);
    }

    public SystemUnderTest setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = new ArrayList<>(ipAddresses);
        return this;
    }

    public SystemUnderTest addIpAddress(String ipAddress) {
        ipAddresses.add(ipAddress);
        return this;
    }

    public SystemUnderTest addIpAddresses(List<String> ipAddresses) {
        this.ipAddresses.addAll(ipAddresses);
        return this;
    }

    public List<String> getHostNames() {
        return new ArrayList<>(hostNames);
    }

    public SystemUnderTest setHostNames(List<String> hostNames) {
        this.hostNames = new ArrayList<>(hostNames);
        return this;
    }

    public SystemUnderTest addHostName(String hostName) {
        hostNames.add(hostName);
        return this;
    }

    public SystemUnderTest addHostNames(List<String> hostNames) {
        this.hostNames.addAll(hostNames);
        return this;
    }

    public boolean isSystemIdentificationValid() {
        return systemIdentification != null;
    }

    public boolean isValid() {
        return isSystemIdentificationValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemUnderTest that)) return false;
        return Objects.equals(systemIdentification, that.systemIdentification)
                && Objects.equals(macAddresses, that.macAddresses)
                && Objects.equals(ipAddresses, that.ipAddresses)
                && Objects.equals(hostNames, that.hostNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemIdentification, macAddresses, ipAddresses, hostNames);
    }
}
