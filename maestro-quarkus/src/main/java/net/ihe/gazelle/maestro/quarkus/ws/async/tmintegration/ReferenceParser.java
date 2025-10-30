/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.quarkus.ws.async.tmintegration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>In Maestro context, a reference is the name of a Property store in TestRun inputs or context
 * To recognize if a String is a reference, it shall follow syntax describ in PATTERN field.
 * </p>
 * <p>
 * For exemple :
 * </p>
 * <p>
 *  <b>${propertyName}</b> is a reference where propertyName is the name of the referenced Property
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
