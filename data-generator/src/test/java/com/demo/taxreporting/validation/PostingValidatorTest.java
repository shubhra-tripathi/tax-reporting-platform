package com.demo.taxreporting.validation;

import com.demo.taxreporting.model.IncomeType;
import com.demo.taxreporting.model.Posting;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class PostingValidatorTest {

    private final PostingValidator validator = new PostingValidator();

    @Test
    void shouldAcceptValidPosting() {
        Posting posting = new Posting(
                "P001",
                "A001",
                OffsetDateTime.parse(
                        "2023-05-06T00:00:00.000+02:00"
                ),
                IncomeType.INTEREST,
                new BigDecimal("500.00"),
                "GBP"
        );

        ValidationResult result =
                validator.validate(posting);

        assertTrue(result.valid());

    }

    @Test
    void shouldRejectValidPostingWithNegativeAmount() {
        Posting posting = new Posting(
                "P002",
                "A001",
                OffsetDateTime.parse(
                        "2026-05-06T00:00:00.000+02:00"
                ),
                IncomeType.INTEREST,
                 new BigDecimal("-100.00"),
                "GBP"
        );
        ValidationResult result = validator.validate(posting);

        assertFalse(result.valid());

        assertTrue(
                result.errors()
                        .contains("AMOUNT_NOT_POSITIVE")
        );

    }
}
