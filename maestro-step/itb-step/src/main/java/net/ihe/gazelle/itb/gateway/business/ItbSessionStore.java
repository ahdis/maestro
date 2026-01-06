package net.ihe.gazelle.itb.gateway.business;

import net.ihe.gazelle.itb.gateway.business.reporting.ItbReporting;

import java.util.concurrent.CompletableFuture;

public interface ItbSessionStore {
   CompletableFuture<ItbReporting> get(String sessionId);

   void add(String sessionId, CompletableFuture<ItbReporting> future);

   void remove(String sessionId);
}
