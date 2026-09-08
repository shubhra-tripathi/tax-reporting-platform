package com.demo.taxreporting.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Transaction(
        long transactionId,
        long customerId,
        OffsetDateTime transactionTimestamp,
        BigDecimal amount,
        String merchantCategory,
        boolean fraud,
        int year,
        int month
) {
}
