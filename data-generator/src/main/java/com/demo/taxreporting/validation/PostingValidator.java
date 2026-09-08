package com.demo.taxreporting.validation;

import com.demo.taxreporting.model.Posting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PostingValidator {

    public ValidationResult validate(Posting posting) {

        if (posting == null) {
            return ValidationResult.failure(
                    "POSTING_NULL"
            );
        }

        List<String> errors = new ArrayList<>();

        if (posting.postingId() == null
                || posting.postingId().isBlank()) {

            errors.add("POSTING_ID_MISSING");
        }

        if (posting.accountId() == null
                || posting.accountId().isBlank()) {

            errors.add("ACCOUNT_ID_MISSING");
        }

        if (posting.amount() == null) {
            errors.add("AMOUNT_MISSING");

        } else if (posting.amount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            errors.add("AMOUNT_NOT_POSITIVE");
        }

        if (posting.postingTimestamp() == null) {
            errors.add("TIMESTAMP_MISSING");
        }

        if (posting.currency() == null
                || posting.currency().isBlank()) {

            errors.add("CURRENCY_MISSING");
        }

        return errors.isEmpty()
                ? ValidationResult.success()
                : new ValidationResult(false, List.copyOf(errors));
    }
}