package com.demo.taxreporting.mapper;

import com.demo.taxreporting.model.IncomeType;
import com.demo.taxreporting.model.Posting;
import com.demo.taxreporting.source.KaggleTransaction;

public class PostingMapper {

    public Posting map(KaggleTransaction source) {

        if(source == null) {
            throw new IllegalArgumentException(
                    "source transaction must not be null"
            );

        }
        return new Posting(
                "P" + source.transactionId(),
                "A" + source.customerId(),
                source.transactionTimestamp(),
                mapIncomeType(source.merchantCategory()),
                source.amount(),
                "GBP"
        );
    }

    private IncomeType mapIncomeType(String merchantCategory) {
        if(merchantCategory == null) {
            return IncomeType.OTHER_INCOME;
        }

        return switch (merchantCategory.toLowerCase()) {
            case "investment" -> IncomeType.DIVIDEND;
            case "banking" -> IncomeType.INTEREST;
            default -> IncomeType.OTHER_INCOME;
        };
    }
}
