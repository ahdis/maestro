package net.ihe.gazelle.maestro.spi.business.recording;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;

/**
 * Service provider loaded through {@link java.util.ServiceLoader} allowing each step
 * module to contribute its own {@link StepOutputDissector}.
 */
public interface StepOutputDissectorFactory {

   /**
    * @return {@code true} when this factory can produce a dissector for the given step
    * @param stepType
    */
   boolean supports(final String stepType);

   /**
    * @return a new dissector instance
    */
   StepOutputDissector create();
}
