package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TestReportSummaryBuilderTest {

    @Test
    void shouldBuildSummaryWithProvidedFields() {
        Instant timestamp = Instant.parse("2025-02-01T12:00:00Z");

        TestReportSummaryBuilder builder = new TestReportSummaryBuilder()
                .setId("summary-42")
                .setDateTime(timestamp)
                .setResult(Result.PASSED);

        TestReportSummary summary = builder.build();

        assertEquals("summary-42", summary.getId());
        assertEquals(timestamp, summary.getDateTime());
        assertSame(Result.PASSED, summary.getResult());
    }
}
