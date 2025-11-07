package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;

/**
 * Abstraction responsible for selecting the appropriate {@link StepOutputDissector}
 * for a given {@link StepRunReport}. Implementations may rely on SPI lookups or
 * contextual information to choose the most suitable dissector.
 */
public interface StepOutputDissectorProvider {

   /**
    * Resolve the dissector to use for the supplied step.
    *
    * @param stepRunReport the step run report to inspect
    * @return never {@code null}; implementations should fall back to a default dissector if no match exists
    */
   StepOutputDissector resolve(StepRunReport stepRunReport);
}
