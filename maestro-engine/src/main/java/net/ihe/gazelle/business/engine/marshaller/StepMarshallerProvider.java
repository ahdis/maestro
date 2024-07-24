package net.ihe.gazelle.business.engine.marshaller;

import net.ihe.gazelle.marshaller.StepMarshaller;

import java.util.Iterator;
import java.util.ServiceLoader;

public class StepMarshallerProvider {
    public StepMarshaller getMarshaller(String type) {
        ServiceLoader<StepMarshaller> marshallers = ServiceLoader.load(StepMarshaller.class);
        Iterator<StepMarshaller> iterator = marshallers.iterator();
        while (iterator.hasNext()) {
            StepMarshaller marshaller = iterator.next();
            if(marshaller.getSupportedStep().equals(type)){
                return marshaller;
            }
        }
        throw new RuntimeException("No marshaller found for type " + type);
    }
}
