package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;

/**
 * Model for text component of a Gazelle report
 */
public class Text implements Serializable {
    private String value;

    private String lang;

    public Text() {
    }

    public Text(String value, String lang) {
        this.value = value;
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
