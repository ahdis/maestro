package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;

/**
 * Service provider loaded through {@link java.util.ServiceLoader} allowing each step
 * module to contribute its own {@link StepOutputDissector}.
 */
public interface StepOutputDissectorFactory {

   /**
    * @param stepRunReport the step run to inspect
    * @return {@code true} when this factory can produce a dissector for the given step
    */
   boolean supports(StepRunReport stepRunReport);

   /**
    * @return a new dissector instance
    */
   StepOutputDissector create();
}
