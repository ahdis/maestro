package net.ihe.gazelle.maestro.testreport;

import net.ihe.gazelle.testreport.UnexpectedError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnexpectedErrorTest {

    @Test
    void unexpectedError_valid() {
        UnexpectedError unexpectedError = new UnexpectedError()
                .setName("name")
                .setMessage("test")
                .setCause(new UnexpectedError());
        assertEquals("name", unexpectedError.getName());
        assertEquals("test", unexpectedError.getMessage());
        assertEquals(new UnexpectedError(), unexpectedError.getCause());
        assertTrue(unexpectedError.isValid());
    }

    @Test
    void unexpectedError_minimum_valid() {
        UnexpectedError unexpectedError = new UnexpectedError()
                .setName("name")
                .setMessage("test");
        assertTrue(unexpectedError.isValid());
    }

    @Test
    void unexpectedError_not_valid_with_blank_name() {
        UnexpectedError unexpectedError = new UnexpectedError()
                .setName("  ")
                .setCause(new UnexpectedError());
        assertFalse(unexpectedError.isValid());
    }
}
