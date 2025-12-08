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

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Business rules <br>
 * all mandatory <br>
 * serviceIdentification.version mandatory (version "unknown" is accepted)
 */
public class TestService implements Serializable {

    @Serial
    private static final long serialVersionUID = 4438062754526037863L;

    private EntityIdentification serviceIdentification;
    private String disclaimer;

    public TestService() {
    }

    public TestService(EntityIdentification serviceIdentification, String disclaimer) {
        setServiceIdentification(serviceIdentification);
        this.disclaimer = disclaimer;
    }

    public EntityIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    public TestService setServiceIdentification(EntityIdentification serviceIdentification) {
        if (serviceIdentification.getVersion() == null) {
            serviceIdentification.setVersion("unknown");
        }
        this.serviceIdentification = serviceIdentification;
        return this;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public TestService setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
        return this;
    }

    public boolean isServiceIdentificationValid() {
        return serviceIdentification != null;
    }

    public boolean isServiceVersionValid() {
        return serviceIdentification.getVersion() != null && !serviceIdentification.getVersion().isBlank();
    }

    public boolean isDisclaimerValid() {
        return disclaimer != null && !disclaimer.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestService that)) return false;
        return Objects.equals(serviceIdentification, that.serviceIdentification)
                && Objects.equals(disclaimer, that.disclaimer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceIdentification, disclaimer);
    }
}
