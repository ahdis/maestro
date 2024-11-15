package report.model;

import net.ihe.gazelle.report.model.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void item_valid_with_bytes() {
        Item item = new Item("name", "bytes".getBytes());
        assertEquals("name", item.getName());
        assertArrayEquals("bytes".getBytes(), item.getContent());
        assertTrue(item.isValid());
    }

    @Test
    void item_valid_with_reference() {
        Item item = new Item("name", "reference");
        assertEquals("name", item.getName());
        assertEquals("reference", item.getReference());
        assertTrue(item.isValid());
    }

    @Test
    void item_not_valid_with_all_fields() {
        Item item = new Item()
                .setName("name")
                .setContent("bytes".getBytes())
                .setReference("reference");
        assertEquals("name", item.getName());
        assertArrayEquals("bytes".getBytes(), item.getContent());
        assertEquals("reference", item.getReference());
        assertFalse(item.isValid());
    }

    @Test
    void item_not_valid_with_only_name() {
        Item item = new Item().setName("name");
        assertEquals("name", item.getName());
        assertFalse(item.isValid());
    }
}
