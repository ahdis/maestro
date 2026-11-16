/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestrorecording.adapter;

import net.ihe.gazelle.maestro.engine.business.TestReportRecordingException;

import java.io.*;

/** Detached binary snapshot preserving polymorphic model values and mutable byte arrays. */
final class RecordingSnapshotFactory {
    <T extends Serializable> T copy(T source) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
                output.writeObject(source);
            }
            try (ObjectInputStream input =
                    new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                @SuppressWarnings("unchecked")
                T copy = (T) input.readObject();
                return copy;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new TestReportRecordingException("Cannot snapshot report for recording", e);
        }
    }
}
