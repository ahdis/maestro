package net.ihe.gazelle.maestro.simulation.step.record;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissector;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.StepOutputDissectorFactory;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;

/**
 * Registers the simulation step output dissector.
 */
public class SimulationStepOutputDissectorFactory implements StepOutputDissectorFactory {

   @Override
    public boolean supports(StepRunReport stepRunReport) {
        return stepRunReport != null
              && SimulationStepDefinition.TYPE.equals(stepRunReport.getType());
    }

    @Override
    public StepOutputDissector create() {
        return new SimulationStepOutputDissector();
    }
}
