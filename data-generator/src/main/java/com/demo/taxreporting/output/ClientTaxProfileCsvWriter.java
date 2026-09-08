package com.demo.taxreporting.output;

import com.demo.taxreporting.model.ClientTaxProfile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientTaxProfileCsvWriter implements AutoCloseable{

    private final BufferedWriter writer;

    public ClientTaxProfileCsvWriter(Path output) throws IOException {

        this.writer = Files.newBufferedWriter(output);
        writer.write("clientId,taxResidence,fatcaStatus,crsStatus");

        writer.newLine();
    }

    public void write(ClientTaxProfile profile) throws IOException {

        writer.write(String.join(",",
                profile.clientId(),
                profile.taxResidence(),
                profile.fatcaStatus().name(),
                profile.crsStatus().name()));

        writer.newLine();
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
