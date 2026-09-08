package com.demo.taxreporting.model;

import java.math.BigDecimal;

public record AccountBalance(
        String accountId,
        String clientId,
        BigDecimal balance,
        String currency
) {
}
