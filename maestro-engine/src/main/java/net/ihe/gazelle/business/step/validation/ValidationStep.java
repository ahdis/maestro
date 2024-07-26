package net.ihe.gazelle.business.step.validation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.step.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationStep implements Step {

    @JsonSubTypes.Type(ValidationStep.class)
    public static String type;
    private String name;
    private String validationService;
    private String validationProfile;
    private String contentToValidate;
    private Map<String, String> properties;

    protected static final String VALIDATION_SERVICE  = "validationService";
    protected static final String VALIDATION_PROFILE  = "validationProfile";
    protected static final String CONTENT_TO_VALIDATE = "contentToValidate";

    public String getValidationService() {
        return validationService;
    }


    public String getValidationProfile() {
        return validationProfile;
    }

    public String getContentToValidate() {
        return contentToValidate;
    }

    public ValidationStep(String name,
                          String validationService,
                          String validationProfile,
                          String contentToValidate) {
        this.name = name;
        this.validationService = validationService;
        this.validationProfile = validationProfile;
        this.contentToValidate = contentToValidate;
    }

    public ValidationStep(String name, Map<String, String> properties){
        this.name = name;
        this.properties = properties;
    }

    public Object getReport(Context context){
        return context.getReport(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getProperties() {
        if(properties == null){
            return new HashMap<>();
        }
        return new HashMap<>(properties);
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = new HashMap<>(properties);
    }


    @Override
    public List<String> getMandatoryKeys() {
        List<String> keys = new ArrayList<>();
        keys.add(VALIDATION_SERVICE);
        keys.add(VALIDATION_PROFILE);
        keys.add(CONTENT_TO_VALIDATE);
        return keys;
    }
}