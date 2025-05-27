package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * An ITB Step is an automated step used to execute a test in ITB.
 * This step has 7 properties.
 */
public class ItbStep implements Step {

    public static final String TEST_CASE = "testCase";
    public static final String SYSTEM_ID = "systemID";
    public static final String ACTOR_ID = "actorID";

    public static final String TYPE = "ITB";

    private String name;
    private Property<String> testCase;
    private Property<String> systemID;
    private Property<String> actorID;
    private Property<byte[]>[] inputProperties;

    public ItbStep() {
    }

    public ItbStep(String name, Property<String> testCase, Property<String> systemID, Property<String> actorID, Property<byte[]>[] inputs){
        this.name = name;
        this.testCase = testCase;
        this.systemID = systemID;
        this.actorID = actorID;
        this.inputProperties = inputs;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Property<String> getTestCase() {
        return testCase;
    }
    public Property<String> getSystemID() {
        return systemID;
    }
    public Property<String> getActorID() {
        return actorID;
    }

    @Override
    public List<Property<?>> getProperties() {
        List<Property<?>> props = new ArrayList<>();
        props.add(testCase);
        props.add(systemID);
        props.add(actorID);
        Collections.addAll(props, inputProperties);
        return props;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperties(List<Property<?>> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("property shouldn't be null");
        }
        properties.forEach(property -> {
            switch (property.getName()) {
                case TEST_CASE -> testCase = (Property<String>) property;
                case SYSTEM_ID -> systemID = (Property<String>) property;
                case ACTOR_ID -> actorID = (Property<String>) property;
                default -> inputProperties = Arrays.stream(inputProperties).map(prop -> {
                    if (prop.getName().equals(property.getName())) {
                        return property;
                    }
                    return prop;
                }).toArray(Property[]::new);
            }
        });
    }

    @Override
    public String getType() {
        return TYPE;
    }
}