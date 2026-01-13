package net.ihe.gazelle.maestro.quarkus.websocket;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class responsible for managing WebSocket sessions.
 * Provides mechanisms to add and remove WebSocket sessions by their session ID.
 * Designed for use in a scoped application context.
 */
@ApplicationScoped
public class WebSocketSessionService {

   private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

   /**
    * Default constructor.
    */
   public WebSocketSessionService() {
      // Empty
   }

   /**
    * Adds a WebSocket session to the managed collection of sessions.
    *
    * @param sessionId the unique identifier for the WebSocket session
    * @param session the WebSocketSession instance to be associated with the provided sessionId
    */
   public void addSession(String sessionId, WebSocketSession session) {
      sessions.put(sessionId, session);
   }

   /**
    * Retrieves a WebSocket session associated with the specified session ID.
    *
    * @param sessionId the unique identifier of the WebSocket session to retrieve
    * @return the WebSocketSession instance associated with the provided session ID,
    *         or null if no session is found for the given ID
    */
   public WebSocketSession getSession(String sessionId) {
      return sessions.get(sessionId);
   }

   /**
    * Removes a WebSocket session from the managed collection of sessions.
    *
    * @param sessionId the unique identifier for the WebSocket session to be removed
    */
   public void removeSession(String sessionId) {
      sessions.remove(sessionId);
   }
}
