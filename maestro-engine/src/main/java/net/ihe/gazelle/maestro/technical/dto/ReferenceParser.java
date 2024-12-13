package net.ihe.gazelle.maestro.technical.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>In Maestro context, a reference is the name of a Property store in TestRun inputs or TestRunSessionStore context
 * {@link net.ihe.gazelle.maestro.TestRun}
 * {@link net.ihe.gazelle.maestro.context.TestRunSessionStore}
 * To recognize if a String is a reference, it shall follow syntax describ in PATTERN field.
 * </p>
 * <p>
 *     For exemple :
 *     <p>
 *         <b>${propertyName}</b> is a reference where propertyName is the name of the referenced Property
 *     </p>
 * </p>
 */
public class ReferenceParser {

    private static final Pattern PATTERN = Pattern.compile("^\\$\\{(\\w+)}$");

    /**
     * @param value a String
     * @return true if
     */
    public boolean isReference(String value) {
        Matcher matcher = PATTERN.matcher(value);
        return matcher.matches();
    }

    /**
     * @param value A String following reference syntax
     * @return The property name contained in reference
     * @throws IllegalArgumentException if it can't extract Property from value
     */
    public String getReference(String value) {
        Matcher matcher = PATTERN.matcher(value);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid reference: " + value);
    }

}
