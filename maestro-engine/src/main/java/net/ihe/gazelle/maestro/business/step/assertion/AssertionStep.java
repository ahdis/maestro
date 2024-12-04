package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionStep implements Step {

    public static final  String ASSERTION = "assertion";
    public static final String ASSERTION_SERVICE = "assertionService";


    public static final String TYPE = "ASSERTION";

    private final String name;
    private Property<byte[]> assertion;
    private Property<String> assertionService;

    public AssertionStep(String name,
                         Property<byte[]> assertion,
                         Property<String> assertionService) {
        this.assertion = assertion;
        this.assertionService = assertionService;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Property<?>> getProperties() {
        List<Property<?>> props = new ArrayList<>();
        props.add(assertion);
        props.add(assertionService);
        return props;
    }

    @Override
    public void setProperty(List<Property<?>> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("property shouldn't be null");
        }
        properties.forEach(property -> {
            switch (property.getName()) {
                case ASSERTION -> assertion = (Property<byte[]>) property;
                case ASSERTION_SERVICE -> assertionService = (Property<String>) property;
            }
        });
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
