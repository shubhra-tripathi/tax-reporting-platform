package com.demo.taxreporting.mapper;

import com.demo.taxreporting.model.AccountBalance;

import java.math.BigDecimal;

public class AccountBalanceMapper {

    public AccountBalance map(long customerId) {
        String accountId = "A" + customerId;
        String clientId = "C" + customerId;

        BigDecimal balance = BigDecimal.valueOf(10000 + (customerId % 50000));

        return new AccountBalance(
                accountId,
                clientId,
                balance,
                "GBP"
        );
    }
}
