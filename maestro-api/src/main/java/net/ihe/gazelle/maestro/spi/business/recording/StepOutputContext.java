package net.ihe.gazelle.maestro.spi.business.recording;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.security.business.acl.AccessControlList;

/**
 * Immutable data exposed to {@link StepOutputDissector} implementations.
 *
 * @param stepRunReport        the step report to dissect
 * @param accessControlList    ACL that must be applied to newly created Datahouse items
 */
public record StepOutputContext(
      StepRunReport stepRunReport,
      AccessControlList accessControlList
) {
}
