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

package net.ihe.gazelle.maestro.business.context;

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
