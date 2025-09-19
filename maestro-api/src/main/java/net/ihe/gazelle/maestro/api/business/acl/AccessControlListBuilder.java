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

package net.ihe.gazelle.maestro.api.business.acl;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.acl.validator.AccessControlListValidator;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.HashSet;
import java.util.Set;

public class AccessControlListBuilder extends AbstractBuilder<AccessControlList> {

    private Set<String> owners = new HashSet<>();
    private Set<String> readers = new HashSet<>();
    private Set<String> editors = new HashSet<>();
    private boolean isPublic = false;
    private String readAccessKey;

    public AccessControlListBuilder() {

    }
    public AccessControlListBuilder(AccessControlList accessControlList) {
        if (accessControlList != null) {
            this.owners = accessControlList.getOwners() != null ? new HashSet<>(accessControlList.getOwners()) : new HashSet<>();
            this.readers = accessControlList.getReaders() != null ? new HashSet<>(accessControlList.getReaders()) : new HashSet<>();
            this.editors = accessControlList.getEditors() != null ? new HashSet<>(accessControlList.getEditors()) : new HashSet<>();
            this.isPublic = accessControlList.isPublic();
            this.readAccessKey = accessControlList.getReadAccessKey();
        }
    }

    public AccessControlListBuilder setOwners(Set<String> owners) {
        this.owners = owners;
        return this;
    }

    public AccessControlListBuilder setReaders(Set<String> readers) {
        this.readers = readers;
        return this;
    }

    public AccessControlListBuilder setEditors(Set<String> editors) {
        this.editors = editors;
        return this;
    }

    public AccessControlListBuilder setPublic(boolean aPublic) {
        isPublic = aPublic;
        return this;
    }

    public AccessControlListBuilder setReadAccessKey(String readAccessKey) {
        this.readAccessKey = readAccessKey;
        return this;
    }

    @Override
    protected AbstractValidator<AccessControlList> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new AccessControlListValidator();
    }

    @Override
    protected AccessControlList instantiate() {
        return new AccessControlList();
    }

    @Override
    protected void make(AccessControlList accessControlList) {
        accessControlList.setOwners(owners);
        accessControlList.setReaders(readers);
        accessControlList.setEditors(editors);
        accessControlList.setPublic(isPublic);
        accessControlList.setReadAccessKey(readAccessKey);
    }
}
