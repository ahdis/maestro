package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.inject.Qualifier;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CDI qualifier used to identify a {@link SessionStore} that stores {@link MaestroObserver}
 * instances associated with individual {@link TestRun} executions.
 */
@Qualifier
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface TestRunObserverStore {
}
