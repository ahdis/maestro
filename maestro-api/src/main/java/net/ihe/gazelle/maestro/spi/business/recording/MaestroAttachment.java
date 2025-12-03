package net.ihe.gazelle.maestro.spi.business.recording;

/**
 * Mutable attachment descriptor used by {@link AttachmentPlan}.
 */
public record MaestroAttachment(byte[] content, String type, String filename) {

}
