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

package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A property containing a byte array
 */
public class ByteArrayProperty extends Property {

   @Serial
   private static final long serialVersionUID = -2541331305238610805L;

   /**
    * The name of the file containing the value.
    */
   private String fileName;

   /**
    * The mime type of the file.
    */
   private String mimeType;

   /**
    * Default constructor
    */
   public ByteArrayProperty() {
      super();
   }

   /**
    * Constructs a new {@code ByteArrayProperty} with the specified name and value.
    *
    * @param name  the name of the property, which uniquely identifies it
    * @param value the value to be assigned to this property; must be a {@code Serializable} object
    */
   public ByteArrayProperty(String name, Serializable value) {
      super(name, value);
   }

    /**
     * Constructs a new {@code ByteArrayProperty} with the specified name and value.
     *
     * @param name  the name of the property, which uniquely identifies it
     * @param value the value to be assigned to this property; must be a {@code Serializable} object
     * @param userId the user ID associated with this property
     * @param dateTime the date and time when this property was created or modified
     */
    public ByteArrayProperty(String name, Serializable value, String userId, Instant dateTime) {
        super(name, value, userId, dateTime);
    }

   /**
    * Retrieves the name of the file associated with this property.
    *
    * @return the file name, or {@code null} if no file name has been specified
    */
   public String getFileName() {
      return fileName;
   }

   /**
    * Sets the name of the file associated with this property and returns the updated instance.
    *
    * @param fileName the name of the file to be associated with this property
    * @return the updated ByteArrayProperty instance with the specified file name
    */
   public ByteArrayProperty setFileName(String fileName) {
      this.fileName = fileName;
      return this;
   }

   /**
    * Retrieves the MIME type of the file associated with this property.
    *
    * @return the MIME type of the file, or {@code null} if no MIME type has been specified
    */
   public String getMimeType() {
      return mimeType;
   }

   /**
    * Sets the MIME type of the file associated with this property and returns the updated instance.
    *
    * @param mimeType the MIME type to be associated with this property
    * @return the updated ByteArrayProperty instance with the specified MIME type
    */
   public ByteArrayProperty setMimeType(String mimeType) {
      this.mimeType = mimeType;
      return this;
   }

   @Override
   public Property copy() {
      return new ByteArrayProperty(getName(), getCopyValue(), getUserId(), getDateTime())
            .setFileName(getFileName())
            .setMimeType(getMimeType());
   }
}
