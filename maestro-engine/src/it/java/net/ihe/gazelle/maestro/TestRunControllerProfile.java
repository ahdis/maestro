package net.ihe.gazelle.maestro;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestRunControllerProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        Map<String, String> overrides = new HashMap<>();
        overrides.put("gzl.service.registry.file.path", this.getClass().getResource("/services.json").getPath());
        return overrides;
    }

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return QuarkusTestProfile.super.getEnabledAlternatives();
    }
}
