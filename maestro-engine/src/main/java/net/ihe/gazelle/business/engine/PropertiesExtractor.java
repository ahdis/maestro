package net.ihe.gazelle.business.engine;

import net.ihe.gazelle.context.Context;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesExtractor {

    public static final String PROPERTY_EXPANSION_REGEXP = "\\$\\{(.+?)\\}";
    public static final int PROPERTY_NAME = 1;

    public Map<String, String> extractProperties(Map<String, String> properties, Context context){
        Iterator<String> it = properties.keySet().iterator();
        String key;
        while(it.hasNext()){
            key = it.next();

            properties.replace(key, expandProperty(properties.get(key), context));
        }
        return properties;
    }

    private String expandProperty(String property, Context context) throws NotInitializedReferenceException{
        if(property == null){
            throw new NullPropertyValue("property value can't be null");
        } else {
            Pattern pattern = Pattern.compile(PROPERTY_EXPANSION_REGEXP);
            Matcher matcher = pattern.matcher(property);
            if (matcher.matches()) {
                String propertyName = matcher.group(PROPERTY_NAME);
                if (context.containsKey(propertyName)) {
                    return (String) context.get(propertyName);
                } else {
                    throw new NotInitializedReferenceException("couldn't find " + propertyName + " in context");
                }
            }
            return property;
        }
    }
}
