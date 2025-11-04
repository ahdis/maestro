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

package net.ihe.gazelle.maestro.quarkus.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.spi.technical.ConfigProvider;
import org.eclipse.microprofile.config.Config;

@Default
@ApplicationScoped
public class ConfigProviderImpl implements ConfigProvider {

    private final Config config;

    @Inject
    public ConfigProviderImpl(Config config) {
        this.config = config;
    }

    @Override
    public String getConfig(String key) {
        return config.getValue(key, String.class);
    }
}
