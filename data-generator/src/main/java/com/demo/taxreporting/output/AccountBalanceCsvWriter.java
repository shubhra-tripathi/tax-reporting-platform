package com.demo.taxreporting.output;

import com.demo.taxreporting.model.AccountBalance;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AccountBalanceCsvWriter implements AutoCloseable{

    private final BufferedWriter writer;

    public AccountBalanceCsvWriter(Path outputPath) throws IOException {
        this.writer = Files.newBufferedWriter(outputPath);
        writer.write("accountId,clientId,balance,currency");
        writer.newLine();
    }

    public void write(AccountBalance account) throws IOException {
        writer.write(String.join(",",
                account.accountId(),
                account.clientId(),
                account.balance().toPlainString(),
                account.currency()));

        writer.newLine();
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
