package com.demo.taxreporting.generator;

public record GenerationStatistics(
        long inputRecords,
        long validRecords,
        long rejectedRecords
) {
}
