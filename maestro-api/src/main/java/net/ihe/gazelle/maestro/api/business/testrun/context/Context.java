package net.ihe.gazelle.maestro.api.business.testrun.context;

import net.ihe.gazelle.maestro.api.business.testrun.Property;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to store a lit of properties usable in e testRun execution context
 * Each Property is identified by its name in properties map
 */
public class Context {

    private final ConcurrentHashMap<String, Property<?>> properties = new ConcurrentHashMap<>();

    @SuppressWarnings("java:S1452")
    public Property<?> getProperty(String key){
        return properties.get(key);
    }

    public boolean containsKey(String key){
        return properties.containsKey(key);
    }

    public void addProperty(String key, Property<?> property){
        properties.put(key, property);
    }

}
