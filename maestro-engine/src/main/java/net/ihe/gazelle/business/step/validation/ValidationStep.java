package net.ihe.gazelle.business.step.validation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.step.Step;

public class ValidationStep implements Step {

    @JsonSubTypes.Type(ValidationStep.class)
    public static String type;
    private String name;
    private String validationService;
    private String validationProfile;
    private String contentToValidate;


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

    public Object getReport(Context context){
        return context.getReport(name);
    }

    @Override
    public String getName() {
        return name;
    }
}