package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.test.Step;

/**
 * Produces events related to the execution of {@link Step} instances.
 */
public interface StepRunEventProducer {

   /**
    * Notifies that a {@link StepRunFinished} event has occurred.
    *
    * @param stepRunFinished the finished step run event to notify
    */
   void notify(StepRunFinished stepRunFinished);

}
