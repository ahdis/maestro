package net.ihe.gazelle.maestro.api.business.testreport.dissection;

/**
 * SPI allowing a step implementation to describe how its outputs should be
 * persisted inside Datahouse.
 */
public interface StepOutputDissector {

   /**
    * Translate the step outputs into a persistence-ready projection.
    *
    * @param context immutable view of the step run, its parent test run and the ACL to apply
    * @return never {@code null}; use {@link StepOutputProjection#empty()} when nothing must be persisted
    */
   StepOutputProjection dissect(StepOutputContext context);
}
