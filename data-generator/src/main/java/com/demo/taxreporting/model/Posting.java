package com.demo.taxreporting.model;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Posting(
        String postingId,
        String accountId,
        OffsetDateTime postingTimestamp,
        IncomeType incomeType,
        BigDecimal amount,
        String currency
){}