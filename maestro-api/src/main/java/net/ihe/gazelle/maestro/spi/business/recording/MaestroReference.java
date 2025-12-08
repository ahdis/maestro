package net.ihe.gazelle.maestro.spi.business.recording;

/**
 * Simple descriptor describing a reference that must be emitted once an item or attachment is persisted.
 */
public record MaestroReference(String type, String name, MaestroRefType refType, String value) {

}
