package net.ihe.gazelle.maestrorecording.adapter;

import java.util.Arrays;
import java.util.Objects;

record ItemPersistenceState(String[] itemIds, String[] itemUrls) {
    static final ItemPersistenceState EMPTY = new ItemPersistenceState(new String[0], new String[0]);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemPersistenceState(String[] ids, String[] urls))) return false;
        return Objects.deepEquals(itemIds, ids) && Objects.deepEquals(itemUrls, urls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(itemIds), Arrays.hashCode(itemUrls));
    }

    @Override
    public String toString() {
        return "ItemPersistenceState{" +
                "itemIds=" + Arrays.toString(itemIds) +
                ", itemUrls=" + Arrays.toString(itemUrls) +
                '}';
    }
}
