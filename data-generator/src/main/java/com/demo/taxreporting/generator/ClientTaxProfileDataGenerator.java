package com.demo.taxreporting.generator;

import com.demo.taxreporting.mapper.ClientTaxProfileMapper;
import com.demo.taxreporting.output.ClientTaxProfileCsvWriter;
import com.demo.taxreporting.source.KaggleTransactionCsvReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ClientTaxProfileDataGenerator {

    private final KaggleTransactionCsvReader reader;
    private final ClientTaxProfileMapper mapper;

    public ClientTaxProfileDataGenerator(KaggleTransactionCsvReader reader, ClientTaxProfileMapper mapper) {
        this.reader = reader;
        this.mapper = mapper;
    }

    public long generate(Path input, Path output) throws Exception {

        Set<Long> customerIds = new HashSet<>();

        try (var transactions = reader.read(input)) {

            transactions.forEach(
                    transaction ->
                            customerIds.add(transaction.customerId())
            );
        }

        try (var writer = new ClientTaxProfileCsvWriter(output)) {

            for (Long customerId : customerIds) {
                writer.write(mapper.map(customerId));
            }
        }

        return  customerIds.size();
    }
}
