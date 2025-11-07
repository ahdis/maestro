package net.ihe.gazelle.maestro.api.business.testreport.dissection;

import com.kereval.gazelle.datahouse.api.business.record.Item;

/**
 * Symbolic handle returned by {@link StepOutputProjectionBuilder#addItem(Item)}.
 * The builder resolves it to an index when {@link #build()} is called.
 */
public final class ItemHandle {
    private final long id;

    ItemHandle(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemHandle that = (ItemHandle) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "ItemHandle{" + "id=" + id + '}';
    }
}
