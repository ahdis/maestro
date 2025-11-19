package net.ihe.gazelle.maestro.api.business.recording;

/**
 * Mutable attachment descriptor used by {@link net.ihe.gazelle.maestro.api.business.testreport.dissection.AttachmentPlan}.
 */
public interface MaestroAttachment {

   byte[] getContent();

   String getType();

   String filename();

}
