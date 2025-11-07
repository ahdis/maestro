package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.property.Property;

/**
 * Functional interface used to rebuild a step property once a Datahouse identifier
 * and the corresponding URL are known.
 */
@FunctionalInterface
public interface PropertyReferenceFactory {

   /**
    * @param itemId  identifier returned by Datahouse
    * @param itemUrl absolute URL to the newly persisted resource
    * @return replacement property
    */
   Property create(String itemId, String itemUrl);
}
