package com.demo.taxreporting.output;

import com.demo.taxreporting.model.Posting;
import com.demo.taxreporting.validation.ValidationResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RejectedPostingCsvWriter implements AutoCloseable{

    private final BufferedWriter writer;


    public RejectedPostingCsvWriter(Path path) throws IOException {
        writer = Files.newBufferedWriter(path);
        writer.write("postingId,accountId,amount,errors");
        writer.newLine();
    }

    public void write(Posting posting, ValidationResult validation) throws IOException {
        writer.write(String.join(",",
                posting.postingId(),
                posting.accountId(),
                posting.amount().toPlainString(),
                String.join("|", validation.errors())
        ));

        writer.newLine();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
