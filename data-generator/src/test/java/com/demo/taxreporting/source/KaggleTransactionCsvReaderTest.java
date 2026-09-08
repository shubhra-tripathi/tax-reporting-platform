package com.demo.taxreporting.source;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KaggleTransactionCsvReaderTest {

    private final KaggleTransactionCsvReader reader = new KaggleTransactionCsvReader();

    @Test
    void shouldReadTransactionsFromCsv() throws Exception {

        Path path = Path.of("src/test/resources/kaggle/transactions-small.csv");

        try(Stream<KaggleTransaction> stream = reader.read(path)) {
            List<KaggleTransaction> transactions = stream.toList();

            assertEquals(2, transactions.size());

            KaggleTransaction first = transactions.getFirst();

            assertEquals(500006L,
                    first.transactionId());
            assertEquals(49108L, first.customerId());

            assertEquals(new BigDecimal("274.13"), first.amount());
            assertEquals("travel", first.merchantCategory());
        }
    }
}
