package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.business.step.itb.gateway.client.ItbHttpClient;
import net.ihe.gazelle.maestro.business.step.itb.gateway.model.request.Parameter;
import net.ihe.gazelle.maestro.business.step.itb.gateway.model.request.TestCase;
import net.ihe.gazelle.maestro.business.step.itb.gateway.model.request.TestRequest;
import net.ihe.gazelle.maestro.business.step.itb.gateway.service.RunItbTestService;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ItbHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(ItbHandler.class);

    protected ItbHandler() {    }

    public void handle(ItbStep step) {
        String baseItbUrl = Objects.requireNonNull(ConfigProvider.getConfig().getValue("itb.api.url", String.class));
        String itbAuthorization = Objects.requireNonNull(ConfigProvider.getConfig().getValue("itb.api.key", String.class));
        ItbHttpClient itbHttpClient = new ItbHttpClient(baseItbUrl, itbAuthorization);
        RunItbTestService runItbTestService = new RunItbTestService(itbHttpClient);

        TestRequest testRequest = stepToTestRequestMapper(step);
        runItbTestService.runTests(testRequest);
    }

    private TestRequest stepToTestRequestMapper(ItbStep step) {
        TestRequest testRequest = new TestRequest();
        testRequest.setTestID(step.getActorID().getValue());
        TestCase testCase = new TestCase();
        Optional<Property<String>> propertyOptional = step.getProperties().stream()
                .filter(property -> "testCase".equals(property.getName()) && property.getValue() instanceof String)
                .map(property -> (Property<String>) property)
                .findFirst();
        testCase.setTestCase(propertyOptional.orElseThrow().getValue());

        List<Parameter> parameters = new ArrayList<>();
        for (Property property : step.getProperties()) {
            Parameter parameter = new Parameter();
            parameter.setName(property.getName());
            try {
                parameter.setType(property.getValue() instanceof byte[] ? "file" : "text");
                if (parameter.getType().equals("file")) {
                    parameter.setValue(new String(Base64.getEncoder().encode((byte[]) property.getValue())));
                } else {
                    parameter.setValue((String) property.getValue());
                }
                parameters.add(parameter);
            } catch (NoSuchElementException e) {
                // Do nothing, this means the property is not set
            }
        }
        testCase.setParameters(parameters);
        testRequest.setParameters(List.of());
        testRequest.setTestCases(List.of(testCase));
        return testRequest;
    }

    /**
     * Override from {@link Handler} Interface
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

}
