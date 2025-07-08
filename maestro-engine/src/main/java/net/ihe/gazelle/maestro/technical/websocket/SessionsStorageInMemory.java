package net.ihe.gazelle.maestro.technical.websocket;

import jakarta.websocket.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionsStorageInMemory {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionsStorageInMemory() {
        // Prevent instantiation
    }

    public static void addSession(String testSetId, Session session) {
        sessions.put(testSetId, session);
    }

    public static Session getSession(String testSetId) {
        return sessions.get(testSetId);
    }

    public static void removeSession(String testSetId) {
        sessions.remove(testSetId);
    }

    public static boolean hasSession(String testSetId) {
        return sessions.containsKey(testSetId);
    }
}
