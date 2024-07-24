package net.ihe.gazelle.step;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.ihe.gazelle.context.Context;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface Step {
    /**
     * Load Step report into context
     * @param context before Report integration
     * @return same context with report integration
     */
    Object getReport(Context context);
    String getName();
}

