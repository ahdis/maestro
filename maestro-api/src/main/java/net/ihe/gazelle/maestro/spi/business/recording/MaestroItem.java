package net.ihe.gazelle.maestro.spi.business.recording;

import net.ihe.gazelle.security.business.ProtectedResource;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.List;
import java.util.Map;

/**
 * Descriptor used by {@link ItemPlan} to prepare items before persistence.
 */
public record MaestroItem(AccessControlList acl,
                          String type,
                          String content,
                          List<MaestroReference> references,
                          Map<String, String> additionalParameters) {
}
