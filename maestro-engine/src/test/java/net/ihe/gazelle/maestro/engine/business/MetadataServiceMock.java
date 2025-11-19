package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.servicemetadata.api.business.MetadataService;
import net.ihe.gazelle.servicemetadata.api.business.Service;

public class MetadataServiceMock implements MetadataService {

   @Override
   public Service getMetadata() {
      return new Service()
              .setName("Maestro Engine")
              .setVersion("999.0.0");
   }
}
