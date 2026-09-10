package com.demo.taxreporting.ingestion;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sfn.SfnClient;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IngestionHandlerTest {

    private final SfnClient sfnClient =
            mock(SfnClient.class);

    private final IngestionHandler handler =
            new IngestionHandler(sfnClient);

    @Test
    void shouldRejectNullEvent() {

        assertThrows(
                IllegalArgumentException.class,
                () -> handler.handleRequest(null, null)
        );
    }
}