package net.ihe.gazelle.business.step;

import net.ihe.gazelle.step.Step;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class StepFactoryProvider {
    public Step create(String type, String stepName, Map<String, String> properties){
        ServiceLoader<StepFactory> stepFactories = ServiceLoader.load(StepFactory.class);
        Iterator<StepFactory> iterator = stepFactories.iterator();
        while (iterator.hasNext()) {
            StepFactory factory = iterator.next();
            if(factory.getSupportedStep().equals(type)){
                return factory.create(properties, stepName);
            }
        }
        throw new NotProvidedStep("Step type " + type + " not provided");
    }
}
