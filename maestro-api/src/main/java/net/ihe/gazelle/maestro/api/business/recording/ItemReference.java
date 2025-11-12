package net.ihe.gazelle.maestro.api.business.recording;

/**
 * A reference to a recorded item with its id and location.
 *
 * @param id       the id of the item
 * @param location the location URL of the item
 */
public record ItemReference(String id, String location) {
}
