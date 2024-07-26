package net.ihe.gazelle.step;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.ihe.gazelle.context.Context;

import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface Step {
    /**
     * Load Step report into context
     * @param context before Report integration
     * @return same context with report integration
     */
    Object getReport(Context context);
    String getName();
    Map<String, String> getProperties();
    void setProperties(Map<String, String> properties);
    List<String> getMandatoryKeys();
}

