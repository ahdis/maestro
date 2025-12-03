package net.ihe.gazelle.maestro.spi.business.recording;

/**
 * Represents the type of reference used when persisting items or attachments.
 */
public enum MaestroRefType {

    /**
     * The reference carries the identifier of another item.
     */
    ITEM_ID,

    /**
     * The reference points to an attachment resource.
     */
    ATTACHMENT,

    /**
     * The reference stores a URL rather than an identifier.
     */
    URL
}
