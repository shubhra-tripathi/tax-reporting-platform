package com.demo.taxreporting.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class IngestionHandlerTest {

    private final WorkflowStarter workflowStarter =
            mock(WorkflowStarter.class);

    private final IngestionHandler handler =
            new IngestionHandler(
                    new FeedTypeResolver(),
                    new ObjectMapper(),
                    workflowStarter
            );

    @Test
    void shouldRejectNullEvent() {

        assertThrows(
                IllegalArgumentException.class,
                () -> handler.handleRequest(null, null)
        );
    }
}