package net.ihe.gazelle.maestro.recording;

record ItemPersistenceState(String[] itemIds, String[] itemUrls) {
    static final ItemPersistenceState EMPTY = new ItemPersistenceState(new String[0], new String[0]);
}
