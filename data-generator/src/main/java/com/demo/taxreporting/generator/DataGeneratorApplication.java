package com.demo.taxreporting.generator;

import com.demo.taxreporting.mapper.AccountBalanceMapper;
import com.demo.taxreporting.mapper.ClientTaxProfileMapper;
import com.demo.taxreporting.mapper.PostingMapper;
import com.demo.taxreporting.source.KaggleTransactionCsvReader;
import com.demo.taxreporting.validation.PostingValidator;

import java.nio.file.Files;
import java.nio.file.Path;

public class DataGeneratorApplication {

    public static void main(String[] args) throws Exception {

        System.out.println(
                "Working directory: "
                        + System.getProperty("user.dir")
        );

        Path input = Path.of(
                "sample-data",
                "raw-kaggle",
                "transactions.csv");

        Path outputDirectory = Path.of(
                "sample-data",
                "generated");

        Files.createDirectories(outputDirectory);
        Path validOutput = outputDirectory.resolve("posting.csv");
        Path rejectedOutput = outputDirectory.resolve("rejected_posting.csv");

        PostingDataGenerator generator = new PostingDataGenerator(new KaggleTransactionCsvReader(),
                new PostingMapper(),
                new PostingValidator());

        long start = System.currentTimeMillis();

        GenerationStatistics stats = generator.generate(input, validOutput, rejectedOutput);
        long duration = System.currentTimeMillis() - start;

        System.out.println("Input records  : " + stats.inputRecords());

        System.out.println("Valid records  : " + stats.validRecords());

        System.out.println("Rejected records  : " + stats.rejectedRecords());

        System.out.println("Duration (ms)   : " + duration);

        Path accountOutput =
                outputDirectory.resolve(
                        "account_balance.csv"
                );

        AccountBalanceDataGenerator accountGenerator =
                new AccountBalanceDataGenerator(
                        new KaggleTransactionCsvReader(),
                        new AccountBalanceMapper()
                );

        long accountCount =
                accountGenerator.generate(
                        input,
                        accountOutput
                );

        System.out.println(
                "Generated accounts : "
                        + accountCount
        );

        Path taxProfileOutput =
                outputDirectory.resolve(
                        "client_tax_profile.csv"
                );

        ClientTaxProfileDataGenerator taxProfileGenerator =
                new ClientTaxProfileDataGenerator(
                        new KaggleTransactionCsvReader(),
                        new ClientTaxProfileMapper()
                );

        long taxProfileCount =
                taxProfileGenerator.generate(
                        input,
                        taxProfileOutput
                );

        System.out.println(
                "Generated tax profiles : "
                        + taxProfileCount
        );
    }
}
