package net.ihe.gazelle.business.engine;

import net.ihe.gazelle.context.Context;

import java.util.Iterator;
import java.util.Map;

public class PropertiesExtractor {

    public static final String PROPERTY_EXPANSION_REGEXP = "\\$\\{((\\w*)\\()?((.*?)\\.)?(.*?)\\)" +
            "?\\}";

    public Map<String, String> extractProperties(Map<String, String> properties, Context context){
        Iterator<String> it = properties.keySet().iterator();
        String key;
        while(it.hasNext()){
            key = it.next();
            properties.replace(key, expandProperty(properties.get(key), context));
        }
        return properties;
    }

    private String expandProperty(String property, Context context){
        if(property.matches(PROPERTY_EXPANSION_REGEXP)){
            if(context.containsKey(property)){
                return (String)context.get(property);
            } else {
                throw new NotInitializedReferenceException("couldn't find " + property + " in context");
            }
        }
        return property;
    }
}
