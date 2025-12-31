package net.ihe.gazelle.maestrorecording.factory;

import com.kereval.gazelle.datahouse.technical.rest.client.RecordItemClientImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import net.ihe.gazelle.maestrorecording.adapter.DatahouseTestReportRecordingService;
import net.ihe.gazelle.maestrorecording.adapter.SPIStepOutputDissectorProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * CDI factory for producing {@link DatahouseTestReportRecordingService} instances.
 */
@ApplicationScoped
public class RecordingServiceCDIFactory {

   @ConfigProperty(name = "gzl.datahouse.url")
   String datahouseUrl;

   private final SPIStepOutputDissectorProvider dissectorProvider = new SPIStepOutputDissectorProvider();

   /**
    * Default constructor
    */
   public RecordingServiceCDIFactory() {
      // Empty
   }

   /**
    * Produces a new {@link DatahouseTestReportRecordingService} for each request.
    *
    * @return a {@code DatahouseTestReportRecordingService} instance configured with the
    *         datahouse URL and {@link SPIStepOutputDissectorProvider}.
    */
   @Produces
   @RequestScoped
   public DatahouseTestReportRecordingService getRecordingService() {
      return new DatahouseTestReportRecordingService(
            new RecordItemClientImpl(datahouseUrl, "gzl.service.k8s.id"),
            datahouseUrl,
            dissectorProvider
      );
   }

}
