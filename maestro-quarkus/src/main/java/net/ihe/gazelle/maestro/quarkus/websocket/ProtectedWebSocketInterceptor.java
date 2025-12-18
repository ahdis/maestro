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

package net.ihe.gazelle.maestro.quarkus.websocket;

import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import net.ihe.gazelle.maestro.quarkus.websocket.dto.AuthaurizationDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.security.business.UnauthorizedException;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.lang.reflect.Method;

@ProtectedWebSocket
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class ProtectedWebSocketInterceptor {

   private final TextSerDes serDes = new JacksonSerDes();
   private final JWTParser parser;
   private final JwtContext jwtContext;

   @Inject
   public ProtectedWebSocketInterceptor(JWTParser parser, JwtContext jwtContext) {
      this.parser = parser;
      this.jwtContext = jwtContext;
   }

   private final String rootTestBedUrl = ConfigProvider.getConfig().getValue("gzl.root.test.bed.url", String.class);

   @AroundInvoke
   Object around(InvocationContext ctx) throws Exception {
      Method method = ctx.getMethod();
      ProtectedWebSocket annotation = findProtectedAnnotation(method, ctx.getTarget().getClass());

      // If annotation found
      if (annotation != null) {
         Object[] params = ctx.getParameters();
         String messageString = (String) params[1];
         try {
            AuthaurizationDTO message = serDes.deserialize(messageString, AuthaurizationDTO.class);
            JsonWebToken jwt = parser.parse(message.getAuthorization());

            // If no jwt provided we don't abort request as this will be translated as an unauthenticated GazelleIdentity
            if (jwt != null && jwt.getClaimNames() != null) {
               assertTokenIsValid(jwt);
               jwtContext.set(jwt);
            }
         } catch (Exception e) {
            jwtContext.set(null);
         }
      }

      return ctx.proceed();
   }

   private ProtectedWebSocket findProtectedAnnotation(Method method, Class<?> targetClass) {
      ProtectedWebSocket annotation = method.getAnnotation(ProtectedWebSocket.class);
      if (annotation != null) {
         return annotation;
      }

      return findAnnotationForInterfaces(method, targetClass);
   }

   private ProtectedWebSocket findAnnotationForInterfaces(Method method, Class<?> targetClass) {
      ProtectedWebSocket annotation;
      for (Class<?> iface : targetClass.getInterfaces()) {
         try {
            Method interfaceMethod = iface.getMethod(method.getName(), method.getParameterTypes());
            annotation = interfaceMethod.getAnnotation(ProtectedWebSocket.class);
            if (annotation != null) {
               return annotation;
            }
         } catch (NoSuchMethodException ignored) {
            return null;
         }
      }
      return null;
   }

   private void assertTokenIsValid(JsonWebToken jwt) {
      if (jwt.getAudience() == null || !jwt.getAudience().contains(rootTestBedUrl))
         throw new UnauthorizedException("Bad audience");

      long currentTimestampSeconds = System.currentTimeMillis() / 1000;
      if (jwt.getIssuedAtTime() > currentTimestampSeconds) {
         throw new UnauthorizedException("Bad issued at time");
      }
      if (currentTimestampSeconds > jwt.getExpirationTime()) {
         throw new UnauthorizedException("Token expired");
      }
   }
}