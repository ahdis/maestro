package net.ihe.gazelle.maestrorecording.adapter;

/**
 * A reference to a recorded item with its id and location.
 *
 * @param id       the id of the item
 * @param location the location URL of the item
 */
record ItemReference(String id, String location) {
}
