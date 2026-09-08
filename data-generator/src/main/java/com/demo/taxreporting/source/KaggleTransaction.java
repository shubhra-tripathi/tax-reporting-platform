package com.demo.taxreporting.source;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record KaggleTransaction(
        long transactionId,
        long customerId,
        OffsetDateTime transactionTimestamp,
        BigDecimal amount,
        String merchantCategory
) {
}
