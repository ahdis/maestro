package net.ihe.gazelle.maestro.api.business.recording;

import java.util.Objects;

/**
 * Simple descriptor describing a reference that must be emitted once an item or attachment is persisted.
 */
public interface MaestroReference {

   String getType();

   String getName();

   MaestroRefType getRefType();

   String getValue();
}
