package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.inject.Qualifier;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CDI qualifier used to identify a {@link SessionStore} that stores {@link TestSuiteSession}
 * instances associated with {@link TestSuiteRun} executions.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface TestSuiteStore {
}
