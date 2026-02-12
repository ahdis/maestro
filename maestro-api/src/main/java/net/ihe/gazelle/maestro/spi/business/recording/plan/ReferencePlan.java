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

package net.ihe.gazelle.maestro.spi.business.recording.plan;

import java.util.Objects;

public class ReferencePlan {

   private final MaestroRefType refType;
   private final String name;
   private final String targetType;
   private final Object target;

   private ReferencePlan(MaestroRefType refType,
                         String name,
                         String targetType,
                         Object target) {
      this.refType = refType;
      this.name = name;
      this.targetType = targetType;
      this.target = target;
   }

   public static ReferencePlan forItem(String name, String targetType, Object target) {
      return new ReferencePlan(MaestroRefType.ITEM_ID, name, targetType, target);
   }

   public static ReferencePlan forAttachment(String name, AttachmentPlan attachmentPlan) {
      return new ReferencePlan(MaestroRefType.ATTACHMENT, name, attachmentPlan.type(), attachmentPlan);
   }

   public static ReferencePlan forURL(String name, String type, String url) {
      return new ReferencePlan(MaestroRefType.URL, name, type, url);
   }

   public MaestroRefType getRefType() {
      return refType;
   }

   public String getName() {
      return name;
   }

   public String getTargetType() {
      return targetType;
   }

   public Object getTarget() {
      return target;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof ReferencePlan that)) {
         return false;
      }
      return refType == that.refType && Objects.equals(name, that.name)
            && Objects.equals(targetType, that.targetType) && Objects.equals(target, that.target);
   }

   @Override
   public int hashCode() {
      return Objects.hash(refType, name, targetType, target);
   }

}
