package com.demo.taxreporting.ingestion;

public record IngestionResult(
        String runId,
        String bucket,
        String objectKey,
        FeedType feedType,
        String status
) {
}
