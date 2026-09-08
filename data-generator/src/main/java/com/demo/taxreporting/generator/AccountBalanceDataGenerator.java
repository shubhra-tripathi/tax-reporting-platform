package com.demo.taxreporting.generator;

import com.demo.taxreporting.mapper.AccountBalanceMapper;
import com.demo.taxreporting.output.AccountBalanceCsvWriter;
import com.demo.taxreporting.source.KaggleTransactionCsvReader;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class AccountBalanceDataGenerator {

    private final KaggleTransactionCsvReader reader;
    private final AccountBalanceMapper mapper;

    public AccountBalanceDataGenerator(KaggleTransactionCsvReader reader, AccountBalanceMapper mapper) {
        this.reader = reader;
        this.mapper = mapper;
    }

    public long generate(Path input, Path output) throws Exception {

        Set<Long> customerIds = new HashSet<>();

        try (var transactions = reader.read(input)) {
            transactions.forEach(
                    transaction ->
                            customerIds.add(
                                    transaction.customerId()
                            )
            );
        }

        try (var writer = new AccountBalanceCsvWriter(output)) {
            for(long customerId : customerIds) {
                var account = mapper.map(customerId);

                writer.write(account);
            }
        }

        return customerIds.size();
    }
}
