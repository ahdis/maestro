package net.ihe.gazelle.maestro.technical;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;

import java.util.List;

public class DummyStep implements Step {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String name) {
        //Empty mock method
    }

    @Override
    public List<Property<?>> getProperties() {
        return List.of();
    }

    @Override
    public void setProperties(List<Property<?>> properties) {
        //Empty mock method
    }

    @Override
    public String getType() {
        return "";
    }
}
