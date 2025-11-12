package net.ihe.gazelle.maestro.api.business.recording;

import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.List;
import java.util.Map;

/**
 * Mutable descriptor used by {@link net.ihe.gazelle.maestro.api.business.testreport.dissection.ItemPlan}
 * to prepare items before persistence.
 */
public interface MaestroItem {

   AccessControlList getAccessControlList();

   String getType();

   String getContent();

   List<? extends MaestroReference> getReferences();

   Map<String, String> getAdditionalParameters();
}
