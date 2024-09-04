package net.ihe.gazelle;

import net.ihe.gazelle.business.engine.PropertiesExtractor;
import net.ihe.gazelle.context.Context;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class PropertiesExtractorTest {
    @Test
    void extractProperties(){
        PropertiesExtractor propertiesExtractor = new PropertiesExtractor();
        Map<String, String> properties = new HashMap<>();
        properties.put("ValidationService", "matchbox");
        properties.put("ValdiationServiceURL", "${matchboxURL}");
        Context context = new Context(new HashMap<>());
        context.put("${matchboxURL}", "www.example.com");
        Map <String, String> result = propertiesExtractor.extractProperties(properties, context);
        Map<String, String> expected = new HashMap<>();
        expected.put("ValidationService", "matchbox");
        expected.put("ValdiationServiceURL", "www.example.com");

        org.junit.jupiter.api.Assertions.assertTrue(expected.equals(result));
    }
}
