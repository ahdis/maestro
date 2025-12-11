/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.quarkus.ws.openapi;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import io.smallrye.openapi.internal.models.ExternalDocumentation;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;

/**
 * This class is used to modify the openapi file generated during the build on runtime.
 * It allows us to set variable that we can only get during runtime (ex: environment variable)
 */
@OpenApiFilter(OpenApiFilter.RunStage.RUN)
public class OpenAPIFilter implements OASFilter {

   @Override
   public void filterOpenAPI(OpenAPI openAPI) {
      //In this method, we retrieve the term of service path to make sure we have the right url depending of the environment this
      //deployed on.
      if (openAPI.getInfo() != null) {
         String termOfService = ConfigProvider.getConfig().getValue("gzl.terms.of.service.url", String.class);
         String maestroUrl = ConfigProvider.getConfig().getValue("gzl.maestro.url", String.class);
         openAPI.getInfo().setTermsOfService(termOfService);
         openAPI.setExternalDocs(new ExternalDocumentation()
               .url(maestroUrl + "/test-execution-api/index.html")
               .description("Test Run Stream API"));
      }
      OASFilter.super.filterOpenAPI(openAPI);
   }
}
