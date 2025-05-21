package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;

import java.util.ArrayList;
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
    public static final String FIRST_REQUEST_HEADER = "first_req_header";
    public static final String FIRST_REQUEST_PAYLOAD = "first_req_payload";
    public static final String FIRST_RESPONSE_HEADER = "first_resp_header";
    public static final String FIRST_RESPONSE_PAYLOAD = "first_resp_payload";

    public static final String TYPE = "ITB";

    private String name;
    private Property<String> testCase;
    private Property<String> systemID;
    private Property<String> actorID;
    private Property<byte[]> firstRequestHeader;
    private Property<byte[]> firstRequestPayload;
    private Property<byte[]> firstResponseHeader;
    private Property<byte[]> firstResponsePayload;

    public ItbStep() {
    }

    public ItbStep(String name, Property<String> testCase, Property<String> systemID, Property<String> actorID,
                   Property<byte[]> firstRequestHeader, Property<byte[]> firstRequestPayload, Property<byte[]> firstResponseHeader, Property<byte[]> firstResponsePayload){
        this.name = name;
        this.testCase = testCase;
        this.systemID = systemID;
        this.actorID = actorID;
        this.firstRequestHeader = firstRequestHeader;
        this.firstRequestPayload = firstRequestPayload;
        this.firstResponseHeader = firstResponseHeader;
        this.firstResponsePayload = firstResponsePayload;
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
    public Property<byte[]> getFirstRequestHeader() {
        return firstRequestHeader;
    }
    public Property<byte[]> getFirstRequestPayload() {
        return firstRequestPayload;
    }
    public Property<byte[]> getFirstResponseHeader() {
        return firstResponseHeader;
    }
    public Property<byte[]> getFirstResponsePayload() {
        return firstResponsePayload;
    }

    @Override
    public List<Property<?>> getProperties() {
        List<Property<?>> props = new ArrayList<>();
        props.add(testCase);
        props.add(systemID);
        props.add(actorID);
        props.add(firstRequestHeader);
        props.add(firstRequestPayload);
        props.add(firstResponseHeader);
        props.add(firstResponsePayload);
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
                case FIRST_REQUEST_HEADER -> firstRequestHeader = (Property<byte[]>) property;
                case FIRST_REQUEST_PAYLOAD -> firstRequestPayload = (Property<byte[]>) property;
                case FIRST_RESPONSE_HEADER -> firstResponseHeader = (Property<byte[]>) property;
                case FIRST_RESPONSE_PAYLOAD -> firstResponsePayload = (Property<byte[]>) property;
                default -> throw new IllegalArgumentException("property " + property.getName()
                        + " is not a validation step property");
            }
        });
    }

    @Override
    public String getType() {
        return TYPE;
    }
}