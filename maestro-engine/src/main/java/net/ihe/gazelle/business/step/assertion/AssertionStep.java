package net.ihe.gazelle.business.step.assertion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.step.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssertionStep implements Step {

    @JsonSubTypes.Type(AssertionStep.class)
    public static String type;
    private final String assertion;
    private final String assertionService;
    private final String name;
    private Map<String, String> properties;

    public AssertionStep(String name,
                         String assertion,
                         String assertionService) {
        this.assertion = assertion;
        this.assertionService = assertionService;
        this.name = name;
    }

    @Override
    public Object getReport(Context context) {
        return context.getReport(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getProperties() {
        return new HashMap<>(properties);
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = new HashMap<>(properties);
    }

    @Override
    public List<String> getMandatoryKeys() {
        return List.of();
    }

    public String getAssertion(){
        return assertion;
    }

    public String getAssertionService() {
        return assertionService;
    }
}
