package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.security.business.acl.AccessControlList;

/**
 * Immutable data exposed to {@link StepOutputDissector} implementations.
 *
 * @param stepRunReport        the step report to dissect
 * @param parentTestRunReport  the parent test run report
 * @param accessControlList    ACL that must be applied to newly created Datahouse items
 */
public record StepOutputContext(
      StepRunReport stepRunReport,
      TestRunReport parentTestRunReport,
      AccessControlList accessControlList
) {
}
