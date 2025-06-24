package net.ihe.gazelle.maestro.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
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
