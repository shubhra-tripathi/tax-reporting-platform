package com.demo.taxreporting.ingestion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class IngestionHandlerTest {

    private final IngestionHandler handler =
            new IngestionHandler();

    @Test
    void shouldRejectNullEvent() {

        assertThrows(
                IllegalArgumentException.class,
                () -> handler.handleRequest(
                        null,
                        null
                )
        );
    }
}