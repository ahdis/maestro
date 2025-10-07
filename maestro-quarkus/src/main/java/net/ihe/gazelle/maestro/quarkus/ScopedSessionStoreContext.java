package net.ihe.gazelle.maestro.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.maestro.business.SessionStoreContextImpl;


@ApplicationScoped
public class ScopedSessionStoreContext extends SessionStoreContextImpl {
}
