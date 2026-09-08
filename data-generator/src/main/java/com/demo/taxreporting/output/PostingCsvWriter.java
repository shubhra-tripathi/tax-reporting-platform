package com.demo.taxreporting.output;

import com.demo.taxreporting.model.Posting;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PostingCsvWriter implements AutoCloseable{

    private final BufferedWriter writer;

    public PostingCsvWriter(Path outputPath) throws IOException {
        this.writer = Files.newBufferedWriter(outputPath);

        writer.write("postingId,accountId,postingTimestamp,incomeType,amount,currency"
        );
        writer.newLine();
    }

    public void write(Posting posting) throws IOException {

        writer.write(String.join(",",
                posting.postingId(),
                posting.accountId(),
                posting.postingTimestamp().toString(),
                posting.incomeType().name(),
                posting.amount().toPlainString(),
                posting.currency()
                ));
        writer.newLine();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
