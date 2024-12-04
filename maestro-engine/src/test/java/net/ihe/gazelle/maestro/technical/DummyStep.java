package net.ihe.gazelle.maestro.technical;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;

import java.util.List;

public class DummyStep implements Step {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of();
    }

    @Override
    public void setProperty(List<Property<?>> properties) {
        //Empty mock metho
    }

    @Override
    public String getType() {
        return "";
    }
}
