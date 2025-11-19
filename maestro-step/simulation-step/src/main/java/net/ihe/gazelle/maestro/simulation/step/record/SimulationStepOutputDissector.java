package net.ihe.gazelle.maestro.simulation.step.record;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.recording.MaestroItem;
import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;
import net.ihe.gazelle.maestro.recording.DatahouseMaestroItem;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.SimulationReportDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Structures the simulation step report into a set of hierarchical Datahouse items.
 */
class SimulationStepOutputDissector implements StepOutputDissector {

    private static final TextSerDes serDes = new JacksonSerDes();

   private static final String ITEM_TYPE_SIMULATION_REPORT = "SIMULATION_REPORT";

   @Override
   public StepOutputProjection dissect(StepOutputContext context) {
      if (context == null || context.stepRunReport() == null) {
         return StepOutputProjection.empty();
      }
      StepRunReport stepRunReport = context.stepRunReport();
      List<Property> outputs = stepRunReport.getOutputs();
      if (outputs == null || outputs.isEmpty()) {
         return StepOutputProjection.empty();
      }

      int reportIndex = findReportOutputIndex(outputs);
      if (reportIndex < 0) {
         return StepOutputProjection.empty();
      }

      ByteArrayProperty reportProperty = (ByteArrayProperty) outputs.get(reportIndex);
      SimulationReport simulationReport = deserializeReport(reportProperty);

      StepOutputGraphBuilder graph = new StepOutputGraphBuilder();
      MaestroItem reportItem = createReportItem(context, simulationReport);
      ItemOutputBuilder reportBuilder = graph.addItem(reportItem);
      //This reference will be added to the enclosing TestRunReport item later on
      reportBuilder.addReferenceBinding(new ReferenceBinding(
              SimulationStepDefinition.REPORT,
              ITEM_TYPE_SIMULATION_REPORT,
              MaestroRefType.ITEM_ID
      ));

      // Replace the original byte[] output with a reference to the persisted report hierarchy.
      PropertyReferenceFactory factory = (itemId, itemUrl) -> {
         ByteArrayItemProperty replacement = new ByteArrayItemProperty(reportProperty);
         replacement.setValue(null);
         return replacement.setReference(itemUrl);
      };
      reportBuilder.addPropertyReplacement(reportIndex, factory);

      return graph.build();
   }

   private MaestroItem createReportItem(StepOutputContext context, SimulationReport simulationReport) {
      SimulationReportDTO dto = new SimulationReportDTO(simulationReport);
      return createItem(context, dto);
   }

   // Reads the binary report output and converts it to the SimulationReport business object.
   private SimulationReport deserializeReport(ByteArrayProperty reportProperty) {
      byte[] value = reportProperty.getValue();
      if (value == null || value.length == 0) {
         throw new IllegalStateException("Simulation report output is empty");
      }
       return serDes.deserialize(value, SimulationReportDTO.class).getBusinessObject();
   }

   // Creates a bare Item with the correct ACL, type, serialized payload, and empty references list.
   private DatahouseMaestroItem createItem(StepOutputContext context, Object payload) {
      DatahouseMaestroItem item = new DatahouseMaestroItem()
            .setAccessControlList(context.accessControlList())
            .setType(ITEM_TYPE_SIMULATION_REPORT)
            .setContent(serialize(payload));
      item.setReferences(new ArrayList<>());
      return item;
   }

   // Serializes DTOs to JSON for persistence in Datahouse.
   private String serialize(Object payload) {
       return serDes.serializeAsString(payload);
   }

   // Finds the index of the REPORT output property so we know which property to mutate later.
   private int findReportOutputIndex(List<Property> outputs) {
      for (int i = 0; i < outputs.size(); i++) {
         Property property = outputs.get(i);
         if (property instanceof ByteArrayProperty && SimulationStepDefinition.REPORT.equals(property.getName())) {
            return i;
         }
      }
      return -1;
   }

}
