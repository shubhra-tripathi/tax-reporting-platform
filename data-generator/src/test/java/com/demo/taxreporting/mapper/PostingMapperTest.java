package com.demo.taxreporting.mapper;

import com.demo.taxreporting.model.IncomeType;
import com.demo.taxreporting.model.Posting;
import com.demo.taxreporting.source.KaggleTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostingMapperTest {

    private final PostingMapper mapper = new PostingMapper();

    @Test
    void shouldMapKaggleTransactionToPosting() {

        KaggleTransaction source = new KaggleTransaction(
                500006L,
                49108L,
                OffsetDateTime.parse(
                        "2026-08-25T10:30:00+01:00"
                ),
                new BigDecimal("274.13"),
                "travel"
        );
        Posting result = mapper.map(source);
        assertEquals("P500006", result.postingId());
        assertEquals("A49108", result.accountId());
        assertEquals( new BigDecimal("274.13"), result.amount());
        assertEquals(IncomeType.OTHER_INCOME, result.incomeType());
        assertEquals("GBP", result.currency());


    }

    @Test
    void shouldRejectNullSourceTransaction() {
        assertThrows(IllegalArgumentException.class,
                () -> mapper.map(null));
    }
}
