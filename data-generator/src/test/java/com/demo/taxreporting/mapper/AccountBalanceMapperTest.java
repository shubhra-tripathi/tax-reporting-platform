package com.demo.taxreporting.mapper;

import com.demo.taxreporting.model.AccountBalance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountBalanceMapperTest {

    private final AccountBalanceMapper mapper = new AccountBalanceMapper();

    @Test
    void shouldCreateAccountFromCustomerId() {

        AccountBalance account = mapper.map(49108L);

        assertEquals("A49108", account.accountId());
        assertEquals("C49108", account.clientId());
        assertEquals("GBP", account.currency());
    }
}
